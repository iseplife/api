package com.iseplife.api.dao.post;

import com.iseplife.api.dto.thread.view.CommentFormView;
import com.iseplife.api.dto.thread.view.CommentView;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.post.Comment;
import com.iseplife.api.services.SecurityService;
import com.iseplife.api.services.ThreadService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CommentFactory {

  @Autowired
  ThreadService threadService;

  @Autowired
  ModelMapper mapper;

  public CommentView toView(CommentProjection comment) {
    mapper.typeMap(CommentProjection.class, CommentView.class).addMappings(mapper -> {
      mapper
        .using(ctx -> threadService.isLiked((Long) ctx.getSource()))
        .map(CommentProjection::getThread, CommentView::setLiked);
      mapper.map(SecurityService::hasRightOn, CommentView::setHasWriteAccess);
    });

    return mapper.map(comment, CommentView.class);
  }

  public CommentFormView toView(Comment comment) {
//    mapper.typeMap(Comment.class, CommentFormView.class).addMappings(mapper -> {
//      mapper
//        .using(ctx -> AuthorFactory.toView((Club) ctx.getSource())).
//        .when(ctx -> ctx.getSource() != null)
//        .map(Comment::getAsClub, CommentFormView::setAuthor
//      );
//    });

    return mapper.map(comment, CommentFormView.class);
  }
}
