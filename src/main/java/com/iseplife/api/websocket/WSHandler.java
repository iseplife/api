package com.iseplife.api.websocket;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.iseplife.api.conf.jwt.JwtTokenUtil;
import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.websocket.handler.PacketHandler;
import com.iseplife.api.websocket.packets.WSPacketIn;
import com.iseplife.api.websocket.packets.WSProtocol;
import com.iseplife.api.websocket.packets.server.WSPSBadToken;
import com.iseplife.api.websocket.packets.server.WSPSConnected;
import com.iseplife.api.websocket.services.WSClientService;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

@Service
public class WSHandler extends TextWebSocketHandler {

  @Autowired
  private WSClientService service;
  
  private WSProtocol protocol = WSProtocol.getInstance();
  
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
      service.addSession(tokenPayload, session);
      new WSPSConnected(tokenPayload.getId()).sendPacket(session);
    } catch (JWTVerificationException e) {
      new WSPSBadToken().sendPacket(session);
    }
  }
  
  @Override
  protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message)  {
    try {
      ByteBuf buf = Unpooled.wrappedBuffer(message.getPayload().array());
      WSPacketIn packet = protocol.readPacket(buf);
      buf.release();
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
