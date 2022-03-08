package com.iseplife.api.websocket.services;

import java.io.IOException;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.iseplife.api.dto.group.view.GroupPreview;
import com.iseplife.api.websocket.packets.server.WSPSGroupJoined;
import com.iseplife.api.websocket.packets.server.WSPSGroupLeft;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WSGroupService {
  @Lazy
  private final WSClientService clientService;

  public void sendJoin(GroupPreview group, Long studentId) {
    WSPSGroupJoined packet = new WSPSGroupJoined(group);
    try {
      clientService.sendPacket(studentId, packet);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public void sendLeave(Long id, Long studentId) {
    WSPSGroupLeft packet = new WSPSGroupLeft(id);
    try {
      clientService.sendPacket(studentId, packet);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
