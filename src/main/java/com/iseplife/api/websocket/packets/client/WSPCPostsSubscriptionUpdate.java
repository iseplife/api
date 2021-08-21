package com.iseplife.api.websocket.packets.client;

import java.io.IOException;

import com.iseplife.api.websocket.packets.WSPacketIn;

import io.netty.buffer.ByteBuf;

public class WSPCPostsSubscriptionUpdate extends WSPacketIn {
  
  private PostSubscription[] posts;
  
  public PostSubscription[] getPosts() {
    return posts;
  }

  @Override
  public void read(ByteBuf buf) throws IOException {
    int size = buf.readUnsignedByte();//Max 256
    
    posts = new PostSubscription[size];
    
    for(int i = 0; i < size; i++)
      posts[i] = new PostSubscription(buf.readUnsignedInt(), buf.readBoolean());
  }
  
  protected class PostSubscription {
    private long id;
    private boolean subscribe;
    public long getId() {
      return id;
    }
    public boolean isSubscribe() {
      return subscribe;
    }
    private PostSubscription(long id, boolean subscribe) {
      this.id = id;
      this.subscribe = subscribe;
    }
  }

}
