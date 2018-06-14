package com.attitude.tinymall.admin.config;

import com.attitude.tinymall.admin.annotation.support.LoginAdminHandlerMethodArgumentResolver;
import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class AdminConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new LoginAdminHandlerMethodArgumentResolver());
    }

    @Value("${wss.server.host}")
    private String host;

    @Value("${wss.server.port}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer()
    {
        com.corundumstudio.socketio.Configuration  config = new com.corundumstudio.socketio.Configuration ();
        config.setHostname(host);
        config.setPort(port);

        //该处可以用来进行身份验证
        config.setAuthorizationListener(data -> {
            //http://localhost:8081?username=test&password=test
            //例如果使用上面的链接进行connect，可以使用如下代码获取用户密码信息，暂时不做身份验证
//              String username = data.getSingleUrlParam("username");
//              String password = data.getSingleUrlParam("password");
            return true;
        });
        return new SocketIOServer(config);
    }

}
