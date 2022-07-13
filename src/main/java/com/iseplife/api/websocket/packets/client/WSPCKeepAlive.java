package com.iseplife.api.websocket.packets.client;

import java.io.IOException;

import com.iseplife.api.websocket.packets.WSPacketIn;

import io.netty.buffer.ByteBuf;

public class WSPCKeepAlive extends WSPacketIn {

  @Override
  public void read(ByteBuf buf) throws IOException { }

}
