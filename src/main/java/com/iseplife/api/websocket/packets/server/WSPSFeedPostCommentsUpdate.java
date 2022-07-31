package com.iseplife.api.websocket.packets.server;

import java.io.IOException;

import com.iseplife.api.websocket.packets.WSPacketOut;

import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WSPSFeedPostCommentsUpdate extends WSPacketOut {

  private final Long threadID;
  private final int comments;

  @Override
  public void write(ByteBuf buf) throws IOException {
    buf.writeInt(threadID.intValue());
    buf.writeInt(comments);
  }

}
