package com.iseplife.api.dto.post.view;

import com.iseplife.api.dto.view.EmbedView;
import com.iseplife.api.dto.view.AuthorView;
import lombok.Data;

import java.util.Date;

@Data
public class PostFormView {
  private Long id;
  private Long thread;
  private Date publicationDate;
  private String description;
  private EmbedView embed;
  private AuthorView author;
  private boolean pinned;
}
