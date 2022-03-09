package com.iseplife.api.dao.post.projection;


import com.iseplife.api.dto.thread.view.CommentView;
import com.iseplife.api.dto.view.AuthorView;
import com.iseplife.api.entity.post.embed.Embedable;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface PostProjection {
    @Value("#{target.p_id}")
    Long getId();
    Long getFeedId();
    Date getPublicationDate();
    String getDescription();
    Embedable getEmbed();

    Long getThread();
    Integer getNbLikes();
    Integer getNbComments();
    boolean isLiked();
    
    @Value("#{target.trendingCommentId == null ? null : new com.iseplife.api.dto.thread.view.CommentView("
        + "target.trendingCommentId,"
        + "target.trendingCommentThreadId,"
        + "new com.iseplife.api.dto.view.AuthorView("
          + "target.trendingCommentAuthorId,"
          + "target.trendingCommentAuthorFeedId,"
          + "target.trendingCommentAuthorType,"
          + "target.trendingCommentAuthorName,"
          + "target.trendingCommentAuthorThumbnail"
        + "), "
        + "target.trendingCommentCreation,"
        + "target.trendingCommentMessage,"
        + "target.trendingCommentLikes,"
        + "target.trendingCommentComments,"
        + "target.trendingCommentLiked,"
        + "target.trendingCommentLastEdition,"
        + "false"
    + ")}")
    CommentView getTrendingComment();
    
    @Value("#{new com.iseplife.api.dto.view.AuthorView("
      + "target.authorId,"
      + "target.authorFeedId,"
      + "target.authorType,"
      + "target.authorName,"
      + "target.authorThumbnail"
    + ")}")
    AuthorView getAuthor();
    /*Integer getTrendingCommentId();
    Integer getTrendingCommentLikes();
    String getTrendingCommentMessage();
    Long getTrendingCommentThreadId();
    Date getTrendingCommentCreation();
    Date getTrendingCommentLastEdition();
    Integer getTrendingCommentComments();*/
}
