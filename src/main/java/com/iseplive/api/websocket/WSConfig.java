package com.iseplive.api.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Created by Guillaume on 29/10/2017.
 * back
 */
@Configuration
@EnableWebSocket
public class WSConfig implements WebSocketConfigurer {

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(postHandler(), "/ws/post").setAllowedOrigins("*");
  }

  @Bean
  public WebSocketHandler postHandler() {
    return new PostHandler();
  }

}
