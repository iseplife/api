package com.iseplife.api.dao.post.projection;

import org.springframework.beans.factory.annotation.Value;

import com.iseplife.api.dao.student.projection.StudentPreviewProjection;

public interface ReportProjection {
  public StudentPreviewProjection getStudent();
  @Value("#{target.post != null ? target.post : null}")
  public PostSimpleProjection getPost();
  @Value("#{target.comment != null ? target.comment : null}")
  public CommentSimpleProjection getComment();
  public Long getId();
}
