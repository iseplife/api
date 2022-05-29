package com.iseplife.api.websocket.packets;

import java.io.IOException;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public abstract class WSPacketOut extends WSPacket {
  public abstract void write(ByteBuf buf) throws IOException;


  public void sendPacket(WebSocketSession session) throws IOException {
    ByteBuf buffer = Unpooled.buffer();
    WSProtocol.getInstance().writePacket(buffer, this);
    byte[] bytes = new byte[buffer.readableBytes()];
    buffer.readBytes(bytes);
    buffer.release();
    session.sendMessage(new BinaryMessage(bytes));
  }
}
