package com.iseplife.api.websocket.packets;

import com.iseplife.api.websocket.packets.client.*;

public enum WSPacketType {
  POSTS_SUBSCRIPTION_UPDATE(0, WSPCPostsSubscriptionUpdate.class);
  
  private int id;
  private Class<? extends WSPacket> packetClass;
  
  public int getId() {
    return id;
  }
  public Class<? extends WSPacket> getPacketClass() {
    return packetClass;
  }
  
  WSPacketType(int id, Class<? extends WSPacket> packetClass){
    this.id = id;
    this.packetClass = packetClass;
  }
}
