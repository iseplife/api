package com.iseplife.api.websocket;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.iseplife.api.conf.jwt.JwtTokenUtil;
import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.websocket.packets.WSProtocol;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Created by Guillaume on 29/10/2017.
 * back
 */
public class WSHandler extends TextWebSocketHandler {

  @Autowired
  private WSService service;
  
  @Autowired
  private WSProtocol protocol;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;
  
  public WSHandler() {
    protocol.init();
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    if (message.getPayloadLength() == 0) return;
    try {
      DecodedJWT jwt = jwtTokenUtil.decodeToken(message.getPayload());
      TokenPayload tokenPayload = jwtTokenUtil.getPayload(jwt);
      service.addSession(tokenPayload.getId(), session);
    } catch (JWTVerificationException e) {
      session.close();
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
	service.removeSession(session);
    session.close(status);
  }
}
