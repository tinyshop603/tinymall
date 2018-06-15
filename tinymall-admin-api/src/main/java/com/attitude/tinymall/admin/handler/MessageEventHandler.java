package com.attitude.tinymall.admin.handler;

import com.attitude.tinymall.core.domain.ClientInfo;
import com.attitude.tinymall.core.domain.MessageInfo;
import com.attitude.tinymall.db.domain.LitemallOrder;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息的集中处理中心
 */
@Component
public class MessageEventHandler {

  private final Log logger = LogFactory.getLog(MessageEventHandler.class);
  private SocketIOServer server;

  private Map<String, ClientInfo> clientInfos = new HashMap<>(2);

  @Autowired
  public void setServer(SocketIOServer server) {
    this.server = server;
  }


  /**
   * 添加connect事件，当客户端发起连接时调用，本文中将clientid与sessionid存入数据库 方便后面发送消息时查找到对应的目标client,
   */
  @OnConnect
  public void onConnect(SocketIOClient client) {
    String clientId = client.getHandshakeData().getSingleUrlParam("clientId");
    ClientInfo clientInfo = clientInfos.get(clientId);
    Date nowTime = new Date(System.currentTimeMillis());
    clientInfo.setConnected((short) 1);
    clientInfo.setMostsignbits(client.getSessionId().getMostSignificantBits());
    clientInfo.setLeastsignbits(client.getSessionId().getLeastSignificantBits());
    clientInfo.setLastconnecteddate(nowTime);
    clientInfos.put(clientId, clientInfo);
    logger.debug(clientId + "已建立连接");
  }

  /**
   * 添加@OnDisconnect事件，客户端断开连接时调用，刷新客户端信息
   */
  @OnDisconnect
  public void onDisconnect(SocketIOClient client) {
    String clientId = client.getHandshakeData().getSingleUrlParam("clientId");
    ClientInfo clientInfo = clientInfos.get(clientId);
    if (null != clientInfo) {
      clientInfo.setConnected((short) 0);
      clientInfo.setMostsignbits(null);
      clientInfo.setLeastsignbits(null);
      clientInfos.put(clientId, clientInfo);
      logger.debug(clientId + "已断开连接");
    }
  }

  /**
   * 消息接收入口，当接收到消息后，查找发送目标客户端，并且向该客户端发送消息，且给自己发送消息
   */
  @OnEvent(value = "orderEvent")
  public void onEvent(SocketIOClient client, AckRequest request, MessageInfo data) {
    String targetClientId = data.getTargetClientId();
    ClientInfo clientInfo = clientInfos.get(targetClientId);
    if (clientInfo != null && clientInfo.getConnected() != 0) {
      UUID uuid = new UUID(clientInfo.getMostsignbits(), clientInfo.getLeastsignbits());
      logger.debug(uuid.toString());
      MessageInfo sendData = new MessageInfo();
      sendData.setSourceClientId(data.getSourceClientId());
      sendData.setTargetClientId(data.getTargetClientId());
      sendData.setMsgType("chat");
      // 此处传递消息应该是订单的信息
      sendData.setMsgContent(data.getMsgContent());
      client.sendEvent("orderEvent", sendData);
      ///发送给管理员的客户端
      server.getClient(uuid).sendEvent("orderEvent", sendData);
    }
  }
}
