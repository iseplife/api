package com.iseplife.api.websocket.packets;

import java.io.IOException;

import io.netty.buffer.ByteBuf;

public abstract class WSPacketIn extends WSPacket {
  public abstract void read(ByteBuf buf) throws IOException;
}
