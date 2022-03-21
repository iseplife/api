package com.iseplife.api.websocket.packets.server;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iseplife.api.dao.subscription.projection.NotificationProjection;
import com.iseplife.api.websocket.packets.NetHelper;
import com.iseplife.api.websocket.packets.WSPacketOut;

import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WSPSNotificationRecieved extends WSPacketOut {
  
  private final NotificationProjection notification;

  @Override
  public void write(ByteBuf buf) throws IOException {
    NetHelper.writeString(buf, new ObjectMapper().writeValueAsString(notification));
  }

}
