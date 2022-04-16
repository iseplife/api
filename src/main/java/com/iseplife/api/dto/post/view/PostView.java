package com.iseplife.api.dto.post.view;

import com.iseplife.api.dao.post.projection.PostContextProjection;
import com.iseplife.api.dao.post.projection.PostProjection;
import com.iseplife.api.dto.view.EmbedView;
import com.iseplife.api.dto.view.AuthorView;
import com.iseplife.api.dto.thread.view.CommentView;
import lombok.Data;

import java.util.Date;

@Data
public class PostView implements PostProjection {
  private Long id;
  private PostContextProjection context;
  private Long thread;
  private Date publicationDate;
  private String description;
  private EmbedView embed;
  private AuthorView author;
  private Integer nbLikes;
  private boolean liked;
  private boolean pinned;
  private boolean homepagePinned;
  private boolean homepageForced;
  private Integer nbComments;
  private CommentView trendingComment;
  private Boolean hasWriteAccess;
}
