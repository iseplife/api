package com.iseplife.api.dao.post;

import com.iseplife.api.dto.view.CommentView;
import com.iseplife.api.entity.post.Comment;
import com.iseplife.api.services.SecurityService;
import com.iseplife.api.services.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CommentFactory {

  @Autowired
  ThreadService threadService;

  public CommentView toView(Comment comment) {
    CommentView commentView = new CommentView();

    commentView.setId(comment.getId());
    commentView.setThread(comment.getThread().getId());
    commentView.setCreation(comment.getCreation());
    commentView.setLikes(comment.getLikes().size());
    commentView.setComments(comment.getThread().getComments().size());

    commentView.setLastEdition(comment.getLastEdition());
    commentView.setMessage(comment.getMessage());

    commentView.setAuthor(
      comment.getAsClub() != null ?
        AuthorFactory.toView(comment.getAsClub()):
        AuthorFactory.toView(comment.getStudent())
    );

    commentView.setLiked(threadService.isLiked(comment));
    commentView.setHasWriteAccess(SecurityService.hasRightOn(comment));

    return commentView;
  }
}
