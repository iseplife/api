package com.iseplife.api.websocket.services;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.services.SubscriptionService;
import com.iseplife.api.websocket.packets.WSPacketOut;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WSClientService {

  private final WSPostService postsService;
  private final SubscriptionService subService;
  
  private Map<Long, Set<WebSocketSession>> clients = new ConcurrentHashMap<>();
  
  public Set<Long> getConnectedStudentIds() {
    return clients.keySet();
  }

  public void addSession(TokenPayload token, WebSocketSession session) {
    session.getAttributes().put("token", token);
    boolean alreadyConnected = clients.containsKey(token.getId());
    if(!alreadyConnected)
      clients.put(token.getId(), ConcurrentHashMap.newKeySet());
    
    clients.get(token.getId()).add(session);
    
    if(!alreadyConnected)
      for(Long feedId : subService.getSubscribedFeeds(token.getId()))
        postsService.addStudentToFeed(token.getId(), feedId);
  }

  public void removeSession(WebSocketSession session) {
    Long id = ((TokenPayload)session.getAttributes().get("token")).getId();
    Set<WebSocketSession> sessions = clients.get(id);
    
    sessions.remove(session);
    
    if(sessions.size() == 0) {
      clients.remove(id);

      postsService.removeStudentFromFeeds(((TokenPayload)session.getAttributes().get("token")).getId());
    }
  }
  
  public void updateToken(Long id, TokenPayload token) {
    Set<WebSocketSession> sessions = clients.get(id);
    for(WebSocketSession session : sessions)
      session.getAttributes().put("token", token);
  }
  
  public void sendPacket(Long studentId, WSPacketOut packet) throws IOException {
    Set<WebSocketSession> sessions = clients.get(studentId);
    if(sessions != null)
      for(WebSocketSession session : sessions)
        packet.sendPacket(session);
  }
  public void broadcastPacket(WSPacketOut packet) {
    for(Set<WebSocketSession> sessions : clients.values())
      for(WebSocketSession session : sessions)
        try {
          packet.sendPacket(session);
        } catch (IOException e) {
          e.printStackTrace();
        }
  }
  public void broadcastPacketIfAccessToOneFeed(WSPacketOut packet, Set<Long> feeds) {
    for(Set<WebSocketSession> sessions : clients.values())
      for(WebSocketSession session : sessions){
        TokenPayload token = (TokenPayload)session.getAttributes().get("token");
        if(!token.getRoles().contains("ROLE_ADMIN"))
          noAccess: {
            for(Long feedId : token.getFeeds())
              if(feeds.contains(feedId))
                break noAccess;
            
            continue;
          }
        try {
          packet.sendPacket(session);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
  }
}
