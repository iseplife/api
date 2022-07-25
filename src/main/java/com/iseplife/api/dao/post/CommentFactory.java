package com.iseplife.api.dao.post;

import com.iseplife.api.dao.post.projection.CommentProjection;
import com.iseplife.api.dto.thread.view.CommentFormView;
import com.iseplife.api.dto.thread.view.CommentView;
import com.iseplife.api.entity.post.Comment;
import com.iseplife.api.services.SecurityService;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CommentFactory {
  final private ModelMapper mapper;
  
  @PostConstruct()
  public void init() {
    mapper.typeMap(CommentProjection.class, CommentView.class)
      .addMappings(mapper -> {
        mapper
          .using(ctx -> SecurityService.hasRightOn((CommentProjection) ctx.getSource()))
          .map(src -> src, CommentView::setHasWriteAccess);
      });
    
    mapper.typeMap(Comment.class, CommentFormView.class)
      .addMappings(mapper -> {
        mapper.map(src -> src.getThread().getId(), CommentFormView::setThread);
        mapper.skip(CommentFormView::setAuthor);
      });
  }

  public CommentView toView(CommentProjection comment) {
    return mapper.map(comment, CommentView.class);
  }

  public CommentFormView toView(Comment comment) {
    CommentFormView view = mapper.map(comment, CommentFormView.class);
    view.setAuthor(comment.getAsClub() != null ?
      AuthorFactory.toView(comment.getAsClub()) :
      AuthorFactory.toView(comment.getStudent())
    );

    return view;
  }
}
