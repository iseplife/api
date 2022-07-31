package com.iseplife.api.websocket.packets.server;

import java.io.IOException;
import java.util.List;

import com.iseplife.api.dao.poll.PollChoiceProjection;
import com.iseplife.api.websocket.packets.WSPacketOut;

import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WSPSFeedPostPollChoiceUpdate extends WSPacketOut {

  private final Long postID;
  private final List<PollChoiceProjection> choices;

  @Override
  public void write(ByteBuf buf) throws IOException {
    buf.writeInt(postID.intValue());
    buf.writeShort(choices.size());
    for(PollChoiceProjection choice : choices) {
      buf.writeInt(choice.getId().intValue());
      buf.writeInt(choice.getVotesNumber());
    }
  }

}
