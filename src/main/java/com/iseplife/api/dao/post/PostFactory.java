package com.iseplife.api.dao.post;

import com.iseplife.api.dao.post.projection.PostProjection;
import com.iseplife.api.dto.post.view.PostFormView;
import com.iseplife.api.dto.post.view.PostView;
import com.iseplife.api.entity.post.Comment;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.post.embed.Embedable;
import com.iseplife.api.services.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PostFactory {
  @Lazy final private CommentFactory commentFactory;
  @Lazy final private EmbedFactory embedFactory;
  final private ModelMapper mapper;


  public PostFormView toFormView(Post post) {
    mapper.typeMap(Post.class, PostFormView.class).addMappings(mapper -> {
      mapper
        .using(ctx -> embedFactory.toView((Embedable) ctx.getSource()))
        .map(Post::getEmbed, PostFormView::setEmbed);
      mapper.map(src -> src.getThread().getId(), PostFormView::setThread);
    });

    return mapper.map(post, PostFormView.class);
  }

  public PostView toView(PostProjection post, Boolean isLiked) {
    mapper
      .typeMap(PostProjection.class, PostView.class)
      .addMappings(mapper -> {
        mapper
          .using(ctx -> SecurityService.hasRightOn((PostProjection) ctx.getSource()))
          .map(src -> src, PostView::setHasWriteAccess);
        mapper
          .using(ctx -> embedFactory.toView((Embedable) ctx.getSource()))
          .map(PostProjection::getEmbed, PostView::setEmbed);
      });
    PostView view = mapper.map(post, PostView.class);
    view.setLiked(isLiked);
    //view.setTrendingComment(commentFactory.toView(trendingComment));

    return view;
  }
}
