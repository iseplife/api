package com.iseplife.api.dao.post;

import com.iseplife.api.dto.view.CommentView;
import com.iseplife.api.entity.post.Comment;
import com.iseplife.api.services.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CommentFactory {

  @Autowired
  ThreadService threadService;

  public CommentView entityToView(Comment comment) {
    CommentView commentView = new CommentView();

    commentView.setId(comment.getId());
    commentView.setCreation(comment.getCreation());
    commentView.setLikes(comment.getLikes());
    commentView.setMessage(comment.getMessage());
    commentView.setStudent(comment.getStudent());

    commentView.setLiked(threadService.isLiked(comment));

    return commentView;
  }
}
