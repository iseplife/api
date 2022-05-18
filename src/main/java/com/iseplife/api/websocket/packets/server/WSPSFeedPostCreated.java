package com.iseplife.api.websocket.packets.server;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iseplife.api.dto.post.view.PostFormView;
import com.iseplife.api.dto.post.view.PostView;
import com.iseplife.api.websocket.packets.NetHelper;
import com.iseplife.api.websocket.packets.WSPacketOut;

import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WSPSFeedPostCreated extends WSPacketOut {

  private final boolean follow;
  private final boolean hasWriteAccess;
  private final PostView post;

  @Override
  public void write(ByteBuf buf) throws IOException {
    buf.writeBoolean(follow);
    buf.writeBoolean(hasWriteAccess);
    NetHelper.writeString(buf, new ObjectMapper().writeValueAsString(post));
  }

}
