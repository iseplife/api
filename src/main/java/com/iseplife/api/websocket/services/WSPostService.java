package com.iseplife.api.websocket.services;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.iseplife.api.dto.post.view.PostFormView;
import com.iseplife.api.websocket.packets.server.WSPSFeedPostCreated;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WSPostService {
  
  private static final Set<Long> EMPTY_SET = new HashSet<>();
  
  @Lazy
  private final WSClientService clientService;

  private Map<Long, Set<Long>> clientsByFeedId = new ConcurrentHashMap<>();

  public void addStudentToFeed(Long clientId, Long feedId) {
    System.out.println("add "+clientId+" to "+feedId);
    clientsByFeedId.computeIfAbsent(feedId, (id) -> new HashSet<>());
    clientsByFeedId.get(feedId).add(clientId);
  }

  public void removeStudentFromFeed(Long clientId, Long feedId) {
    Set<Long> clients = clientsByFeedId.get(clientId);
    if(clients != null)
      clients.remove(feedId);
  }
  public void removeStudentFromFeeds(Long clientId) {
	  for(Set<Long> feed : clientsByFeedId.values())
		  feed.remove(clientId);
  }
  
  public void broadcastPost(PostFormView post) {
    Set<Long> followers = clientsByFeedId.getOrDefault(post.getFeedId(), EMPTY_SET);
    for(Long studentId : clientService.getConnectedStudentIds())
      try {
        clientService.sendPacket(studentId, new WSPSFeedPostCreated(followers.contains(studentId), post));
      } catch (IOException e) {
        e.printStackTrace();
      }
  }
}
