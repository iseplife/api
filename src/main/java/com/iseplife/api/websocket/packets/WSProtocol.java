package com.iseplife.api.websocket.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WSProtocol {
  private Map<Integer, Class<? extends WSPacketIn>> packetIn = new HashMap<>();
  private Map<Class<? extends WSPacketOut>, Integer> packetOut = new HashMap<>();
  
  @SuppressWarnings("unchecked")
  public void init() {
    for(WSPacketType type : WSPacketType.values())
      if(WSPacketIn.class.isAssignableFrom(type.getPacketClass()))
        packetIn.put(type.getId(), (Class<? extends WSPacketIn>) type.getPacketClass());
      else if(WSPacketOut.class.isAssignableFrom(type.getPacketClass()))
        packetOut.put((Class<? extends WSPacketOut>) type.getPacketClass(), type.getId());
  }
  
  @SuppressWarnings("deprecation")
  public WSPacketIn readPacket(DataInputStream stream) throws InstantiationException, IllegalAccessException, IOException{
    int id = stream.readUnsignedByte();
    Class<? extends WSPacketIn> packetClass = packetIn.get(id);
    
    WSPacketIn object = packetClass.newInstance();
    object.read(stream);
    
    return object;
  }
  
  public void writePacket(DataOutputStream stream, WSPacketOut packet) throws IOException {
    stream.writeByte(packetOut.get(packet.getClass()));
    packet.write(stream);
  }
}
