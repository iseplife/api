package com.iseplive.api.websocket;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.iseplive.api.conf.jwt.JwtTokenUtil;
import com.iseplive.api.conf.jwt.TokenPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Created by Guillaume on 29/10/2017.
 * back
 */
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
