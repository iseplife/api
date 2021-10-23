package com.iseplife.api.dto.thread.view;

import com.iseplife.api.dto.view.AuthorView;
import lombok.Data;

import java.util.Date;

@Data
public class CommentFormView {
  private Long id;
  private Long thread;
  private AuthorView author;
  private String message;
  private Date lastEdition;
}
