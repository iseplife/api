package com.iseplife.api.websocket.packets.server;

import java.io.IOException;

import com.iseplife.api.websocket.packets.WSPacketOut;

import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WSPSGroupLeft extends WSPacketOut {

  private final Long id;

  @Override
  public void write(ByteBuf buf) throws IOException {
    buf.writeInt(id.intValue());
  }

}
