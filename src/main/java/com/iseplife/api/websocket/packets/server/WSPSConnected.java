package com.iseplife.api.websocket.packets.server;

import java.io.IOException;

import com.iseplife.api.websocket.packets.WSPacketOut;

import io.netty.buffer.ByteBuf;

public class WSPSConnected extends WSPacketOut {
  
  private long id;
  public WSPSConnected(long id) {
    this.id = id;
  }

  @Override
  public void write(ByteBuf buf) throws IOException {
    buf.writeInt((int)id);
  }

}
