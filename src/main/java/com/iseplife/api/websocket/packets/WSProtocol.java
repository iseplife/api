package com.iseplife.api.websocket.packets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

import com.iseplife.api.websocket.packets.client.WSPCFeedSubscriptionUpdate;
import com.iseplife.api.websocket.packets.client.WSPCPostsSubscriptionUpdate;
import com.iseplife.api.websocket.packets.server.WSPSConnected;

import io.netty.buffer.ByteBuf;

@Service
public class WSProtocol {
  
  private static WSProtocol instance = new WSProtocol().init();
  
  private List<Class<? extends WSPacketIn>> packetIn = new CopyOnWriteArrayList<>();
  private Map<Class<? extends WSPacketOut>, Integer> packetOut = new ConcurrentHashMap<>();

  private int registerPacketServer(Class<? extends WSPacketOut> packetClass) {
      this.packetOut.put(packetClass, this.packetOut.size());
      return this.packetOut.size() - 1;
  }

  private int registerPacketClient(Class<? extends WSPacketIn> packetClass) {
    this.packetIn.add(packetClass);
    return this.packetIn.size() - 1;
  }

  public WSProtocol init() {
    //Server packets
    registerPacketServer(WSPSConnected.class);
    
    //Client packets
    registerPacketClient(WSPCPostsSubscriptionUpdate.class);
    registerPacketClient(WSPCFeedSubscriptionUpdate.class);
    return this;
  }
  
  @SuppressWarnings("deprecation")
  public WSPacketIn readPacket(ByteBuf buf) throws InstantiationException, IllegalAccessException, IOException{
    int id = buf.readUnsignedByte();
    Class<? extends WSPacketIn> packetClass = packetIn.get(id);
    
    WSPacketIn object = packetClass.newInstance();
    object.read(buf);
    
    return object;
  }
  
  public void writePacket(ByteBuf buf, WSPacketOut packet) throws IOException {
    buf.writeByte(packetOut.get(packet.getClass()));
    packet.write(buf);
  }
  
  public static WSProtocol getInstance() {
    return instance;
  }
}
