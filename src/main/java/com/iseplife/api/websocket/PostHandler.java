package com.iseplife.api.websocket;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.iseplife.api.conf.jwt.JwtTokenUtil;
import com.iseplife.api.conf.jwt.TokenPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class PostHandler extends TextWebSocketHandler {
  @Autowired
  private PostMessageService postMessageService;
  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    if (message.getPayloadLength() == 0) return;
    try {
      DecodedJWT jwt = jwtTokenUtil.decodeToken(message.getPayload());
      TokenPayload tokenPayload = jwtTokenUtil.getPayload(jwt);
      postMessageService.addSession(tokenPayload.getId(), session);
    } catch (JWTVerificationException e) {
      session.close();
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    postMessageService.removeSession(session);
    session.close(status);
  }
}
