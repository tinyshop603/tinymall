package com.attitude.tinymall.admin.runner;

import com.corundumstudio.socketio.SocketIOServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ServerRunner  implements CommandLineRunner {
  private final SocketIOServer server;
  private final Log logger = LogFactory.getLog(ServerRunner.class);
  @Autowired
  public ServerRunner(SocketIOServer server) {
    this.server = server;
  }

  @Override
  public void run(String... args) throws Exception {
    server.startAsync();
    logger.debug("连接初始化已完成");
  }
}
