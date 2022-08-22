package com.iseplife.api.websocket.packets.server;

import java.io.IOException;

import com.iseplife.api.websocket.packets.WSPacketOut;

import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WSPSFeedPostOwnLikeUpdate extends WSPacketOut {

  private final Long threadID;
  private final boolean like;

  @Override
  public void write(ByteBuf buf) throws IOException {
    buf.writeInt(threadID.intValue());
    buf.writeBoolean(like);
  }

}
