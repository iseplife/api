package com.iseplife.api.dao.post;

import javax.annotation.PostConstruct;

import com.iseplife.api.dto.poll.view.PollView;
import com.iseplife.api.dto.post.view.PostContextView;
import com.iseplife.api.entity.feed.Feed;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.iseplife.api.dao.poll.PollFactory;
import com.iseplife.api.dao.post.projection.PostProjection;
import com.iseplife.api.dao.post.projection.PostSimpleProjection;
import com.iseplife.api.dao.post.projection.ReportProjection;
import com.iseplife.api.dto.post.view.PostFormView;
import com.iseplife.api.dto.post.view.PostView;
import com.iseplife.api.dto.post.view.ReportView;
import com.iseplife.api.dto.thread.view.CommentView;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.post.embed.Embedable;
import com.iseplife.api.services.SecurityService;
import com.iseplife.api.entity.Thread;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class PostFactory {
  @Lazy final private EmbedFactory embedFactory;
  final private ModelMapper mapper;
  final private PollFactory pollFactory;

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
      });
    mapper.typeMap(PostSimpleProjection.class, PostView.class)
      .addMappings(mapper -> {
        mapper
          .using(ctx -> embedFactory.toView((Embedable) ctx.getSource()))
          .map(PostSimpleProjection::getEmbed, PostView::setEmbed);
      });

    mapper.typeMap(Post.class, PostView.class)
      .addMappings(mapper -> {
        mapper.skip(PostView::setLiked);
        mapper
          .using(ctx -> ((Thread)ctx.getSource()).getId())
          .map(Post::getThread, PostView::setThread);
        mapper
          .using(ctx -> SecurityService.hasRightOn((Post)ctx.getSource()))
          .map(src -> src, PostView::setHasWriteAccess);
        mapper
          .using(ctx -> new PostContextView((Feed) ctx.getSource()))
          .map(Post::getFeed, PostView::setContext);
        mapper
          .using(ctx -> embedFactory.toView((Embedable) ctx.getSource()))
          .map(Post::getEmbed, PostView::setEmbed);
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
    return toFormView(post, null);
  }
  public PostFormView toFormView(Post post, Long studentId) {
    PostFormView view = mapper.map(post, PostFormView.class);
    if(post.getLinkedClub() != null)
    	view.setAuthor(AuthorFactory.toView(post.getLinkedClub()));
    
    if(view.getEmbed() instanceof PollView)
      pollFactory.fillChoices((PollView) view.getEmbed(), studentId);
    return view;
  }

  public ReportView toView(ReportProjection report, Long studentId) {
    ReportView view = mapper.map(report, ReportView.class);
    
    if(view.getPost() != null && view.getPost().getEmbed() instanceof PollView && studentId != null)
      pollFactory.fillChoices((PollView) view.getPost().getEmbed(), studentId);

    return view;
  }
  
  public PostView toView(PostProjection post, CommentView trendingComment, Long studentId) {
    PostView view = mapper.map(post, PostView.class);
    view.setTrendingComment(trendingComment);
    
    if(view.getEmbed() instanceof PollView && studentId != null)
      pollFactory.fillChoices((PollView) view.getEmbed(), studentId);

    return view;
  }
  
  public PostView toView(Post post, Boolean isLiked, CommentView trendingComment, Long studentId) {
    PostView view = mapper.map(post, PostView.class);
    view.setTrendingComment(trendingComment);
    view.setLiked(isLiked);
    if(post.getLinkedClub() != null)
      view.setAuthor(AuthorFactory.toView(post.getLinkedClub()));
    
    if(view.getEmbed() instanceof PollView)
      pollFactory.fillChoices((PollView) view.getEmbed(), studentId);

    return view;
  }
}
