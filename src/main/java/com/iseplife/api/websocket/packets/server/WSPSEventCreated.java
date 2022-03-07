package com.iseplife.api.websocket.packets.server;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iseplife.api.dto.event.view.EventPreview;
import com.iseplife.api.websocket.packets.NetHelper;
import com.iseplife.api.websocket.packets.WSPacketOut;

import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WSPSEventCreated extends WSPacketOut {
  
  private final EventPreview event;

  @Override
  public void write(ByteBuf buf) throws IOException {
    NetHelper.writeString(buf, new ObjectMapper().writeValueAsString(event));
  }

}
