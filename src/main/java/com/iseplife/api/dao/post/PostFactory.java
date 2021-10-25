package com.iseplife.api.dao.post;

import com.iseplife.api.dao.post.projection.PostProjection;
import com.iseplife.api.dto.post.view.PostFormView;
import com.iseplife.api.dto.post.view.PostView;
import com.iseplife.api.dto.thread.view.CommentView;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.post.embed.Embedable;
import com.iseplife.api.services.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PostFactory {
  final private ThreadService threadService;
  final private ModelMapper mapper;


  public PostFormView toPostFormView(Post post) {
    mapper.typeMap(Post.class, PostFormView.class).addMappings(mapper -> {
      mapper
        .using(ctx -> EmbedFactory.toView((Embedable) ctx.getSource()))
        .map(Post::getEmbed, PostFormView::setEmbed);
      mapper.map(src -> src.getThread().getId(), PostFormView::setThread);
    });

    return mapper.map(post, PostFormView.class);
  }

  public PostView toView(PostProjection post) {
    mapper
      .typeMap(PostProjection.class, PostView.class)
      .addMappings(mapper -> {
        mapper
          .using(ctx -> SecurityService.hasRightOn((PostProjection) ctx.getSource()))
          .map(src -> src, PostView::setHasWriteAccess);
        mapper
          .using(ctx -> EmbedFactory.toView((Embedable) ctx.getSource()))
          .map(PostProjection::getEmbed, PostView::setEmbed);
        mapper
          .using(ctx -> threadService.isLiked((Long) ctx.getSource()))
          .map(PostProjection::getThread, PostView::setLiked);
        mapper
          .using(ctx -> threadService.getTrendingComment((Long) ctx.getSource()))
          .map(PostProjection::getThread, PostView::setTrendingComment);
      });

    return mapper.map(post, PostView.class);
  }
}
