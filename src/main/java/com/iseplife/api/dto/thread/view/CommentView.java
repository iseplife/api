package com.iseplife.api.dto.thread.view;
import com.iseplife.api.dao.post.projection.CommentProjection;
import com.iseplife.api.dto.view.AuthorView;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class CommentView implements CommentProjection {
  private Long id;
  private Long thread;
  private AuthorView author;
  private Date creation;
  private String message;
  private Integer likes;
  private Integer comments;
  private Boolean liked;
  private Date lastEdition;
  private Boolean hasWriteAccess;
}
