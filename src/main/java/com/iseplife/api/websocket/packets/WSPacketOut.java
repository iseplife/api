package com.iseplife.api.websocket.packets;

import java.io.DataOutputStream;
import java.io.IOException;

public interface WSPacketOut extends WSPacket {
  public void write(DataOutputStream stream) throws IOException;
}
