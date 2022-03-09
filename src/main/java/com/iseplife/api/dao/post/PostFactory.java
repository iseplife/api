package com.iseplife.api.dao.post;

import java.math.BigInteger;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.iseplife.api.dao.post.projection.CommentProjection;
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
        mapper
          .using(ctx -> SecurityService.hasRightOn((PostProjection) ctx.getSource()))
          .map(src -> src, PostView::setHasWriteAccess);
        mapper
          .using(ctx -> embedFactory.toView((Embedable) ctx.getSource()))
          .map(PostProjection::getEmbed, PostView::setEmbed);
        
     /*   mapper
          .map(PostProjection::getTrendingCommentId, (src, value) -> src.getTrendingComment().setId((Long)value));
        mapper
          .map(PostProjection::getTrendingCommentMessage, (src, value) -> src.getTrendingComment().setMessage((String)value));
        mapper
          .map(PostProjection::getTrendingCommentThreadId, (src, value) -> src.getTrendingComment().setThread((Long)value));
        mapper
          .map(PostProjection::getTrendingCommentLiked, (src, value) -> src.getTrendingComment().setLiked((Boolean)value));
        mapper
          .map(PostProjection::getTrendingCommentLikes, (src, value) -> src.getTrendingComment().setLikes((Integer)value));
        mapper
          .map(PostProjection::getTrendingCommentCreation, (src, value) -> src.getTrendingComment().setCreation((Date)value));
        mapper
          .map(PostProjection::getTrendingCommentComments, (src, value) -> src.getTrendingComment().setComments((Integer)value));
        */
        /*mapper
          .skip(PostProjection::getTrendingComment, PostView::setTrendingComment);
        
        mapper
          .when(src -> src.getSource() != null)
          .map(PostProjection::getTrendingComment, PostView::setTrendingComment);
        */
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
    PostFormView view = mapper.map(post, PostFormView.class);
    if(post.getLinkedClub() != null)
    	view.setAuthor(AuthorFactory.toView(post.getLinkedClub()));
    return view;
  }

  public PostView toView(PostProjection post) {
    return mapper.map(post, PostView.class);
  }
}
