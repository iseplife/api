package com.iseplife.api.dao.post;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.iseplife.api.dao.post.projection.PostProjection;
import com.iseplife.api.dto.post.view.PostFormView;
import com.iseplife.api.dto.post.view.PostView;
import com.iseplife.api.dto.thread.view.CommentView;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.post.embed.Embedable;
import com.iseplife.api.services.SecurityService;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class PostFactory {
  @Lazy final private EmbedFactory embedFactory;
  final private ModelMapper mapper;
  
  @PostConstruct
  public void init() {
    mapper.typeMap(PostProjection.class, PostView.class)
      .addMappings(mapper -> {
        mapper.skip(PostView::setLiked);
        mapper
          .using(ctx -> SecurityService.hasRightOn((PostProjection) ctx.getSource()))
          .map(src -> src, PostView::setHasWriteAccess);
        mapper
          .using(ctx -> embedFactory.toView((Embedable) ctx.getSource()))
          .map(PostProjection::getEmbed, PostView::setEmbed);
      });
    
    mapper.typeMap(Post.class, PostFormView.class)
      .addMappings(mapper -> {
        mapper
          .using(ctx -> embedFactory.toView((Embedable) ctx.getSource()))
          .map(Post::getEmbed, PostFormView::setEmbed);
        mapper.map(src -> src.getThread().getId(), PostFormView::setThread);
      });

  }

  public PostFormView toFormView(Post post) {
    return mapper.map(post, PostFormView.class);
  }

  public PostView toView(PostProjection post, Boolean isLiked, CommentView trendingComment) {
    PostView view = mapper.map(post, PostView.class);
    view.setLiked(isLiked);
    view.setTrendingComment(trendingComment);

    return view;
  }
}
