package com.iseplive.api.dao.post;

import com.iseplive.api.dto.view.CommentView;
import com.iseplive.api.entity.post.Comment;
import com.iseplive.api.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Guillaume on 16/08/2017.
 * back
 */
@Component
public class CommentFactory {

  @Autowired
  PostService postService;

  public CommentView entityToView(Comment comment) {
    CommentView commentView = new CommentView();

    commentView.setId(comment.getId());
    commentView.setCreation(comment.getCreation());
    commentView.setLikes(comment.getLikes());
    commentView.setMessage(comment.getMessage());
    commentView.setStudent(comment.getStudent());

    commentView.setLiked(postService.isCommentLiked(comment));

    return commentView;
  }
}
