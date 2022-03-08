package com.iseplife.api.dto.post.view;

import java.util.Date;

import com.iseplife.api.dto.thread.view.CommentView;
import com.iseplife.api.dto.view.AuthorView;
import com.iseplife.api.dto.view.EmbedView;

import lombok.Data;

@Data
public class PostView {
  private Long id;
  private Long feedId;
  private Long thread;
  private Date publicationDate;
  private String description;
  private EmbedView embed;
  private AuthorView author;
  private Integer nbLikes;
  private boolean liked;
  private boolean pinned;
  private Integer nbComments;
  private CommentView trendingComment;
  private Boolean hasWriteAccess;
}
