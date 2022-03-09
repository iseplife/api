package com.iseplife.api.dto.view;

import com.iseplife.api.constants.AuthorType;
import com.iseplife.api.dao.post.projection.AuthorProjection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorView implements AuthorProjection {
  private Long id;
  private Long feedId;
  private AuthorType authorType;
  private String name;
  private String thumbnail;
}
