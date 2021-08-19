package com.iseplife.api.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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

  @Autowired
  private WSHandler handler;
  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(handler, "/ws").setAllowedOrigins("*");
  }

}
