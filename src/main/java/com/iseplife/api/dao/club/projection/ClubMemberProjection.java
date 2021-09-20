package com.iseplife.api.dao.club.projection;

import com.iseplife.api.constants.ClubRole;
import com.iseplife.api.dao.student.projection.StudentPreviewProjection;

public interface ClubMemberProjection {
  Long getId();
  String getPosition();
  ClubRole getRole();
  StudentPreviewProjection getStudent();
  Long getParent();
}
