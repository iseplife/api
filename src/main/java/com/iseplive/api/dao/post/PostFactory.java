package com.iseplive.api.dao.post;

import com.iseplive.api.constants.Roles;
import com.iseplive.api.dto.PostDTO;
import com.iseplive.api.dto.view.PostView;
import com.iseplive.api.entity.Post;
import com.iseplive.api.entity.user.Student;
import com.iseplive.api.services.AuthService;
import com.iseplive.api.services.ClubService;
import com.iseplive.api.services.PostService;
import com.iseplive.api.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Guillaume on 28/07/2017.
 * back
 */
@Component
public class PostFactory {

  @Autowired
  PostService postService;

  @Autowired
  AuthService authService;

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
    postView.setNbLikes(post.getLike().size());
    postView.setPinned(post.getPinned());
    postView.setNbComments(post.getComments().size());


    postView.setMedia(post.getMedia());
    postView.setAuthor(post.getAuthor());

    postView.setLiked(postService.isPostLiked(post));
    if (authService.isUserAnonymous()) {
      postView.setHasWriteAccess(false);
    } else {
      Student user = authService.getLoggedUser();
      postView.setPrivate(post.getPrivate());
      if (post.getLinkedClub() != null) {
        boolean isAdmin = clubService.getAdmins(post.getLinkedClub()).contains(user);
        postView.setHasWriteAccess(isAdmin);
      }else {
        postView.setHasWriteAccess(post.getAuthor().equals(user));
      }

      if (user.getRoles().contains(studentService.getRole(Roles.ADMIN))) {
        postView.setHasWriteAccess(true);
      }
      if (user.getRoles().contains(studentService.getRole(Roles.POST_MANAGER))) {
        postView.setHasWriteAccess(true);
      }
    }

    return postView;
  }
}
