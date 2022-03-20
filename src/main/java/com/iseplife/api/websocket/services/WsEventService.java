package com.iseplife.api.websocket.services;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.iseplife.api.dao.event.EventFactory;
import com.iseplife.api.dto.event.view.EventPreview;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.websocket.packets.server.WSPSEventCreated;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WsEventService {
  @Lazy
  private final WSClientService clientService;
  final private EventFactory eventFactory;
  
  public void broadcastEvent(Event event) {
    EventPreview preview = eventFactory.toPreview(event);
    WSPSEventCreated packet = new WSPSEventCreated(preview);
    if(event.getTargets().size() == 0)
      clientService.broadcastPacket(packet);
    else
      clientService.broadcastPacketIfAccessToOneFeed(packet, preview.getTargets());
  }
}
