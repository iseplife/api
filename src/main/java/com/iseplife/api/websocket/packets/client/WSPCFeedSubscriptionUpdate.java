package com.iseplife.api.websocket.packets.client;

import java.io.IOException;

import com.iseplife.api.websocket.packets.WSPacketIn;

import io.netty.buffer.ByteBuf;

public class WSPCFeedSubscriptionUpdate extends WSPacketIn {
  
  private long feedId;
  private boolean subscribe;
  public long getFeedId() {
    return feedId;
  }
  public boolean isSubscribe() {
    return subscribe;
  }
  
  @Override
  public void read(ByteBuf buf) throws IOException {
    feedId = buf.readUnsignedInt();
    subscribe = buf.readBoolean();
  }
}
