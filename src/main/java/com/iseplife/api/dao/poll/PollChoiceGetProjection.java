package com.iseplife.api.dao.poll;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import com.iseplife.api.dao.student.projection.StudentPreviewProjection;

public interface PollChoiceGetProjection {
  @Value("#{target.p.id}")
  Long getId();
  @Value("#{target.p.content}")
  String getContent();
  @Value("#{target.p.votes.![student]}")
  List<StudentPreviewProjection> getStudents();
}
