package com.iseplife.api.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import lombok.RequiredArgsConstructor;


/**
 * Created by Guillaume on 29/10/2017.
 * back
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WSConfig implements WebSocketConfigurer {

  private final WSHandler handler;

  @Value("${cors.allowed-origin}")
  private String allowedOrigins;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(handler, "/ws").setAllowedOriginPatterns(allowedOrigins.replaceAll("'", "").split(","));
  }

}
