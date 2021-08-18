package com.iseplife.api.websocket.services;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

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


  public void addSession(Long id, WebSocketSession session) {
    session.getAttributes().put("logged_id", id);
    clients.computeIfAbsent(id, k -> ConcurrentHashMap.newKeySet());
    clients.get(id).add(session);
  }

  public void removeSession(WebSocketSession session) {
    clients.get(session.getAttributes().get("logged_id")).remove(session);
  }
}
