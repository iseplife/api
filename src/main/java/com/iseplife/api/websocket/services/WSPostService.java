package com.iseplife.api.websocket.services;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dao.post.PostFactory;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.services.SecurityService;
import com.iseplife.api.websocket.packets.server.WSPSFeedPostCreated;
import com.iseplife.api.websocket.packets.server.WSPSFeedPostEdited;
import com.iseplife.api.websocket.packets.server.WSPSFeedPostLikesUpdate;
import com.iseplife.api.websocket.packets.server.WSPSFeedPostRemoved;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WSPostService {
  
  private static final Set<Long> EMPTY_SET = new HashSet<>();
  
  @Lazy private final WSClientService clientService;
  private final PostFactory postFactory;

  private Map<Long, Set<Long>> clientsByFeedId = new ConcurrentHashMap<>();

  public void addStudentToFeed(Long clientId, Long feedId) {
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
  
  public void broadcastPost(Post post) {
    Set<Long> followers = clientsByFeedId.getOrDefault(post.getFeed().getId(), EMPTY_SET);
    for(Long studentId : clientService.getConnectedStudentIds()) {
      TokenPayload token = clientService.getToken(studentId);
      if(SecurityService.hasReadAccess(post.getFeed(), token))
        try {
          clientService.sendPacket(studentId, new WSPSFeedPostCreated(followers.contains(studentId), SecurityService.hasRightOn(post, token), postFactory.toFormView(post)));
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
  }
  public void broadcastRemove(Long postId) {
    WSPSFeedPostRemoved packet = new WSPSFeedPostRemoved(postId);
    for(Long studentId : clientService.getConnectedStudentIds())
      try {
        clientService.sendPacket(studentId, packet);
      } catch (IOException e) {
        e.printStackTrace();
      }
  }
  public void broadcastEdit(Post editedPost) {
    WSPSFeedPostEdited packet = new WSPSFeedPostEdited(postFactory.toFormView(editedPost));
    for(Long studentId : clientService.getConnectedStudentIds())
      try {
        clientService.sendPacket(studentId, packet);
      } catch (IOException e) {
        e.printStackTrace();
      }
  }
  public void broadcastLikeChange(Long threadID, int likes) {
    WSPSFeedPostLikesUpdate packet = new WSPSFeedPostLikesUpdate(threadID, likes);
    for(Long studentId : clientService.getConnectedStudentIds())
      try {
        clientService.sendPacket(studentId, packet);
      } catch (IOException e) {
        e.printStackTrace();
      }
  }
}
