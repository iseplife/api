package com.iseplife.api.dao.post;

import com.iseplife.api.dao.post.projection.CommentProjection;
import com.iseplife.api.dto.thread.view.CommentFormView;
import com.iseplife.api.dto.thread.view.CommentView;
import com.iseplife.api.entity.post.Comment;
import com.iseplife.api.services.SecurityService;
import com.iseplife.api.services.ThreadService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CommentFactory {
  final private ThreadService threadService;
  final private ModelMapper mapper;

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
    mapper.typeMap(Comment.class, CommentFormView.class).addMappings(mapper -> {
      mapper.map(src -> src.getThread().getId(), CommentFormView::setThread);
      mapper.skip(CommentFormView::setAuthor);
    });
    CommentFormView view = mapper.map(comment, CommentFormView.class);
    view.setAuthor(comment.getAsClub() != null ?
      AuthorFactory.toView(comment.getAsClub()) :
      AuthorFactory.toView(comment.getStudent())
    );

    return view;
  }
}
