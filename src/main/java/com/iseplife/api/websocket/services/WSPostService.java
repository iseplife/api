package com.iseplife.api.websocket.services;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dao.poll.PollChoiceProjection;
import com.iseplife.api.dao.post.PostFactory;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.services.SecurityService;
import com.iseplife.api.websocket.packets.server.WSPSFeedPostCommentsUpdate;
import com.iseplife.api.websocket.packets.server.WSPSFeedPostCreated;
import com.iseplife.api.websocket.packets.server.WSPSFeedPostEdited;
import com.iseplife.api.websocket.packets.server.WSPSFeedPostLikesUpdate;
import com.iseplife.api.websocket.packets.server.WSPSFeedPostPollChoiceUpdate;
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
    boolean plannedPost = post.getPublicationDate().after(new Date());
    Set<Long> followers = clientsByFeedId.getOrDefault(post.getFeed().getId(), EMPTY_SET);
    for(Long studentId : clientService.getConnectedStudentIds()) {
      TokenPayload token = clientService.getToken(studentId);
      if(plannedPost ? SecurityService.hasRightOn(post, token) : SecurityService.hasReadAccess(post.getFeed(), token))
        try {
          clientService.sendPacket(
            studentId,
            new WSPSFeedPostCreated(followers.contains(studentId),
              SecurityService.hasRightOn(post, token),
              postFactory.toView(post, false, null, null))
          );
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
  }
  public void broadcastCommentsUpdate(Long threadId, int comments) {
    WSPSFeedPostCommentsUpdate packet = new WSPSFeedPostCommentsUpdate(threadId, comments);
    for(Long studentId : clientService.getConnectedStudentIds())
      try {
        clientService.sendPacket(studentId, packet);
      } catch (IOException e) {
        e.printStackTrace();
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
    boolean customDate = editedPost.getPublicationDate().after(new Date());
    WSPSFeedPostEdited packet = new WSPSFeedPostEdited(postFactory.toFormView(editedPost));
    for(Long studentId : clientService.getConnectedStudentIds()) {
      TokenPayload token = clientService.getToken(studentId);
      if(customDate ? SecurityService.hasRightOn(editedPost, token) : SecurityService.hasReadAccess(editedPost.getFeed(), token))
        try {
          clientService.sendPacket(studentId, packet);
        } catch (IOException e) {
          e.printStackTrace();
        }
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
  public void broadcastPollChange(Long postId, List<PollChoiceProjection> pollChoices) {
    WSPSFeedPostPollChoiceUpdate packet = new WSPSFeedPostPollChoiceUpdate(postId, pollChoices);
    for(Long studentId : clientService.getConnectedStudentIds())
      try {
        clientService.sendPacket(studentId, packet);
      } catch (IOException e) {
        e.printStackTrace();
      }
  }
}
