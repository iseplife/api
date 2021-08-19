package com.iseplife.api.websocket.handler;

import com.iseplife.api.websocket.packets.WSPacketIn;

public @interface PacketListener {
  Class<? extends WSPacketIn> clazz();
}
