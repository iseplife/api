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
    WSPSEventCreated packet = new WSPSEventCreated(event);
    if(event.getTargets().size() == 0)
      clientService.broadcastPacket(packet);
    else
      clientService.broadcastPacketIfAccessToOneFeed(packet, event.getTargets());
  }
}
