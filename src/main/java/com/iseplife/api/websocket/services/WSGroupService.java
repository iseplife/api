package com.iseplife.api.websocket.services;

import java.io.IOException;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.iseplife.api.conf.jwt.JwtTokenUtil;
import com.iseplife.api.conf.jwt.TokenSet;
import com.iseplife.api.dto.group.view.GroupPreview;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.websocket.packets.server.WSPSGroupJoined;
import com.iseplife.api.websocket.packets.server.WSPSGroupLeft;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WSGroupService {
  @Lazy
  private final WSClientService clientService;
  private final JwtTokenUtil jwtTokenUtil;

  public void sendJoin(GroupPreview group, Student student) {
    TokenSet token = jwtTokenUtil.generateToken(student);
    WSPSGroupJoined packet = new WSPSGroupJoined(group, token);
    clientService.updateToken(student.getId(), jwtTokenUtil.getPayload(jwtTokenUtil.decodeToken(token.getToken())));
    try {
      clientService.sendPacket(student.getId(), packet);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public void sendLeave(Long id, Student student) {
    TokenSet token = jwtTokenUtil.generateToken(student);
    WSPSGroupLeft packet = new WSPSGroupLeft(id, token);
    try {
      clientService.updateToken(student.getId(), jwtTokenUtil.getPayload(jwtTokenUtil.decodeToken(token.getToken())));
      clientService.sendPacket(student.getId(), packet);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
