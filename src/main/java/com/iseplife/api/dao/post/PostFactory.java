package com.iseplife.api.dao.post;

import com.iseplife.api.dto.PostDTO;
import com.iseplife.api.dto.view.PostView;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.services.*;
import com.iseplife.api.constants.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Guillaume on 28/07/2017.
 * back
 */
@Component
public class PostFactory {

  @Autowired
  AuthService authService;

  @Autowired
  ThreadService threadService;

  @Autowired
  PostService postService;

  @Autowired
  StudentService studentService;

  @Autowired
  ClubService clubService;

  public Post dtoToEntity(PostDTO post) {
    Post p = new Post();
    p.setTitle(post.getTitle());
    p.setDescription(post.getContent());
    p.setPrivate(post.getPrivate());
    return p;
  }

  public PostView entityToView(Post post) {
    PostView postView = new PostView();

    postView.setId(post.getId());
    postView.setTitle(post.getTitle());
    postView.setDescription(post.getDescription());
    postView.setCreationDate(post.getCreationDate());
    postView.setNbLikes(post.getLikes().size());
    postView.setPinned(post.getPinned());
    postView.setNbComments(post.getComments().size());


    postView.setEmbed(post.getEmbed());
    postView.setAuthor(post.getAuthor());

    postView.setLiked(threadService.isLiked(post));

    postView.setPrivate(post.getPrivate());

    postView.setHasWriteAccess(authService.hasRightOn(post));

    return postView;
  }
}