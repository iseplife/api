package com.iseplife.api.websocket.services;

import java.io.IOException;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.iseplife.api.dao.subscription.projection.NotificationProjection;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.websocket.packets.server.WSPSNotificationRecieved;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WSNotificationService {
  @Lazy
  private final WSClientService clientService;
  
  public void broadcastNotification(NotificationProjection notif, Iterable<Student> students) {
    WSPSNotificationRecieved packet = new WSPSNotificationRecieved(notif);
    for(Student student : students)
      try {
        clientService.sendPacket(student.getId(), packet);
      } catch (IOException e) {
        e.printStackTrace();
      }
  }
}
