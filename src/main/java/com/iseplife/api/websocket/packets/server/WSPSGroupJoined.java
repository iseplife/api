package com.iseplife.api.websocket.packets.server;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iseplife.api.conf.jwt.TokenSet;
import com.iseplife.api.dto.group.view.GroupPreview;
import com.iseplife.api.websocket.packets.NetHelper;
import com.iseplife.api.websocket.packets.WSPacketOut;

import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WSPSGroupJoined extends WSPacketOut {

  private final GroupPreview group;
  private final TokenSet jwt;

  @Override
  public void write(ByteBuf buf) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    NetHelper.writeString(buf, objectMapper.writeValueAsString(group));
    NetHelper.writeString(buf, objectMapper.writeValueAsString(jwt));
  }

}
