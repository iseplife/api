package com.iseplife.api.websocket;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.iseplife.api.conf.jwt.JwtTokenUtil;
import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.websocket.handler.PacketHandler;
import com.iseplife.api.websocket.packets.WSPacketIn;
import com.iseplife.api.websocket.packets.WSProtocol;
import com.iseplife.api.websocket.services.WSClientService;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.BinaryMessage;
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
  private WSClientService service;
  
  @Autowired
  private WSProtocol protocol;
  
  @Autowired
  private PacketHandler packetHandler;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

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
  protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message)  {
    DataInputStream stream = new DataInputStream(new ByteArrayInputStream(message.getPayload().array()));
    try {
      WSPacketIn packet = protocol.readPacket(stream);
      packetHandler.fire(packet, session);
    } catch (InstantiationException | IllegalAccessException | IOException e) {
      //Bad packet
      try {
        session.close(CloseStatus.BAD_DATA);
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }
    
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
	service.removeSession(session);
    session.close(status);
  }
}
