package com.iseplife.api.websocket;

import com.iseplife.api.entity.post.Post;
import com.iseplife.api.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Service
@RequiredArgsConstructor
public class PostMessageService {
  final private JsonUtils jsonUtils;
  final private Logger LOG = LoggerFactory.getLogger(PostMessageService.class);

  final private Map<Long, Set<WebSocketSession>> clients = new ConcurrentHashMap<>();


  void addSession(Long id, WebSocketSession session) {
    clients.computeIfAbsent(id, k -> ConcurrentHashMap.newKeySet());
    clients.get(id).add(session);
  }

  void removeSession(WebSocketSession session) {
    clients.forEach((id, sessions) -> {
      if (sessions.contains(session)) {
        clients.get(id).remove(session);
      }
    });
  }

  public void broadcastPost(Long senderId, Post p) {
    String message = jsonUtils.serialize(p);
    clients.forEach((id, sessions) -> {
      if (!id.equals(senderId)) {
        sessions.forEach(s -> {
          try {
            s.sendMessage(new TextMessage(message));
          } catch (IOException e) {
            LOG.error(e.getMessage(), e);
          }
        });
      }
    });
  }
}
