package com.iseplife.api.websocket.packets;

import java.io.DataInputStream;
import java.io.IOException;

public interface WSPacketIn extends WSPacket {
  public void read(DataInputStream stream) throws IOException;
}
