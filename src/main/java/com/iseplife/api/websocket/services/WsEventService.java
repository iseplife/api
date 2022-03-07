package com.iseplife.api.websocket.services;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.iseplife.api.dto.event.view.EventPreview;
import com.iseplife.api.websocket.packets.server.WSPSEventCreated;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WsEventService {
  @Lazy
  private final WSClientService clientService;
  
  public void broadcastNotification(EventPreview event) {
    clientService.broadcastPacket(new WSPSEventCreated(event));
  }
}
