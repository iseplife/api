package com.iseplife.api.dao.post;

import com.iseplife.api.dto.PostDTO;
import com.iseplife.api.dto.view.PostView;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Guillaume on 28/07/2017.
 * back
 */
@Component
public class PostFactory {


  @Autowired
  ThreadService threadService;

  public Post dtoToEntity(PostDTO post) {
    Post p = new Post();
    p.setDescription(post.getDescription());
    p.setPrivate(post.getPrivate());
    return p;
  }

  public PostView entityToView(Post post) {
    PostView postView = new PostView();

    postView.setId(post.getId());
    postView.setDescription(post.getDescription());
    postView.setPublicationDate(post.getPublicationDate());
    postView.setNbLikes(post.getLikes().size());
    postView.setPinned(post.getPinned());
    postView.setNbComments(post.getComments().size());

    postView.setEmbed(post.getEmbed());

    postView.setThread(post.getThread().getId());
    postView.setAuthor(post.getAuthor());
    postView.setLiked(threadService.isLiked(post.getThread()));
    postView.setPrivate(post.getPrivate());

    postView.setHasWriteAccess(AuthService.hasRightOn(post));

    return postView;
  }
}
