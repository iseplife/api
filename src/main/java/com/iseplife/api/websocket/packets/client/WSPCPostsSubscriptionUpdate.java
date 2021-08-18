package com.iseplife.api.websocket.packets.client;

import java.io.DataInputStream;
import java.io.IOException;

import com.iseplife.api.websocket.packets.WSPacketIn;

public class WSPCPostsSubscriptionUpdate implements WSPacketIn {
  
  private long[] postsIds;
  private boolean subscribe;
  
  public long[] getPostsIds() {
    return postsIds;
  }
  public boolean isSubscribe() {
    return subscribe;
  }

  @Override
  public void read(DataInputStream stream) throws IOException {
    subscribe = stream.readBoolean();
    
    int size = stream.readUnsignedByte();//Max 256
    
    postsIds = new long[size];
    
    for(int i = 0; i < size; i++)
      postsIds[i] = stream.readLong();
  }

}
