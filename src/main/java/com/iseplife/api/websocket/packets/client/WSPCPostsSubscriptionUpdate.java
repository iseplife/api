package com.iseplife.api.websocket.packets.client;

import java.io.IOException;

import com.iseplife.api.websocket.packets.WSPacketIn;

import io.netty.buffer.ByteBuf;

public class WSPCPostsSubscriptionUpdate extends WSPacketIn {
  
  private long[] postsIds;
  private boolean subscribe;
  
  public long[] getPostsIds() {
    return postsIds;
  }
  public boolean isSubscribe() {
    return subscribe;
  }

  @Override
  public void read(ByteBuf buf) throws IOException {
    subscribe = buf.readBoolean();
    
    int size = buf.readUnsignedByte();//Max 256
    
    postsIds = new long[size];
    
    for(int i = 0; i < size; i++)
      postsIds[i] = buf.readLong();
  }

}
