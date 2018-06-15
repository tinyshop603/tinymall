package com.attitude.tinymall.core.config;

import com.attitude.tinymall.core.domain.MessageInfo;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.corundumstudio.socketio.listener.DataListener;
import io.socket.client.IO;
import io.socket.client.Socket;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketConfig {

  @Value("${privilege.wss.server.host}")
  public String host;

  @Value("${privilege.wss.server.port}")
  public Integer port;

  @Bean
  public SocketIOServer socketIOServer() {
    com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
    config.setHostname(host);
    config.setPort(port);

    final SocketIOServer server = new SocketIOServer(config);
    server.addEventListener("chatevent", MessageInfo.class, new DataListener<MessageInfo>() {
      @Override
      public void onData(SocketIOClient client, MessageInfo data, AckRequest ackRequest) {
        // broadcast messages to all clients
        server.getBroadcastOperations().sendEvent("chatevent", data);
      }
    });

    return server;
  }


  @Bean
  public Socket socket() throws URISyntaxException {
    IO.Options options = new IO.Options();
    options.transports = new String[]{"websocket"};
    // 重连尝试次数
    options.reconnectionAttempts = Integer.MAX_VALUE;
    // 失败重连的时间间隔(ms)
    options.reconnectionDelay = 1000;
    // 连接超时时间(ms)
    options.timeout = 20000;
    options.forceNew = true;
    //设置此连接只为微信服务端的连接
    options.query = "clientId=wx-api";
    return IO.socket("http://"+host+":"+port+"/", options);
  }

  @Bean
  public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
    return new SpringAnnotationScanner(socketServer);
  }

}
