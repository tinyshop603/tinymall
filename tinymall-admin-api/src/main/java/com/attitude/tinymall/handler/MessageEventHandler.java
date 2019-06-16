package com.attitude.tinymall.handler;

import com.alibaba.fastjson.JSONObject;
import com.attitude.tinymall.common.SocketEvent;
import com.attitude.tinymall.domain.ClientInfo;
import com.attitude.tinymall.domain.MessageInfo;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息的集中处理中心
 */
@Component
@Slf4j
public class MessageEventHandler {

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
    if (null == clientInfo) {
      clientInfo = new ClientInfo();
    }
    Date nowTime = new Date(System.currentTimeMillis());
    clientInfo.setConnected((short) 1);
    clientInfo.setMostsignbits(client.getSessionId().getMostSignificantBits());
    clientInfo.setLeastsignbits(client.getSessionId().getLeastSignificantBits());
    clientInfo.setLastconnecteddate(nowTime);
    clientInfos.put(clientId, clientInfo);
    log.info(clientId + "已建立连接");
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
    }
    log.info(clientId + "已断开连接");
  }

  /**
   * 消息接收入口，当接收到消息后，查找发送目标客户端，并且向该客户端发送消息，且给自己发送消息 实时提交订单
   */
  @OnEvent(value = SocketEvent.SUBMIT_ORDER)
  public void onEventOrderSubmit(SocketIOClient client, AckRequest request, String jsonData) {
    emitDataOnEvent(SocketEvent.SUBMIT_ORDER, jsonData);
  }

  /**
   * 消息接收入口，当接收到消息后，查找发送目标客户端，并且向该客户端发送消息，且给自己发送消息 实时取消订单
   */
  @OnEvent(value = SocketEvent.CANCEL_ORDER)
  public void onEventOrderCancel(SocketIOClient client, AckRequest request, String jsonData) {
    emitDataOnEvent(SocketEvent.CANCEL_ORDER, jsonData);
  }

  @OnEvent(value = SocketEvent.CONFIRM_ORDER)
  public void onEventOrderConfirm(SocketIOClient client, AckRequest request, String jsonData) {
    emitDataOnEvent(SocketEvent.CONFIRM_ORDER, jsonData);
  }

  @OnEvent(value = SocketEvent.REFUND_ORDER)
  public void onEventOrderRefund(SocketIOClient client, AckRequest request, String jsonData) {
    emitDataOnEvent(SocketEvent.REFUND_ORDER, jsonData);
  }


  private void emitDataOnEvent(String eventName, String jsonData) {
    log.info("准备发送事件名称 {}, 预计发送的数据: {}", eventName, jsonData);
    MessageInfo data = JSONObject.parseObject(jsonData, MessageInfo.class);
    String targetClientId = data.getTargetClientId();
    ClientInfo clientInfo = clientInfos.get(targetClientId);
    if (clientInfo != null && clientInfo.getConnected() != 0) {
      UUID uuid = new UUID(clientInfo.getMostsignbits(), clientInfo.getLeastsignbits());
      String jsonOrder = JSONObject.toJSONString(data.getDomainData());
      ///发送给管理员的客户端
      server.getClient(uuid).sendEvent(eventName, jsonOrder);
      log.info("send {} message to target client {}, detail data is {}", eventName, targetClientId,
          jsonOrder);
    }
  }


}
