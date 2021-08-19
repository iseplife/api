package com.iseplife.api.websocket.services;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.iseplife.api.conf.jwt.TokenPayload;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Guillaume on 29/10/2017.
 * back
 */
@Service
public class WSClientService {

  private Map<Long, Set<WebSocketSession>> clients = new ConcurrentHashMap<>();

  public void addSession(TokenPayload token, WebSocketSession session) {
    session.getAttributes().put("token", token);
    clients.computeIfAbsent(token.getId(), k -> ConcurrentHashMap.newKeySet());
    clients.get(token.getId()).add(session);
  }

  public void removeSession(WebSocketSession session) {
    clients.get(((TokenPayload)session.getAttributes().get("logged_id")).getId()).remove(session);
  }
}
