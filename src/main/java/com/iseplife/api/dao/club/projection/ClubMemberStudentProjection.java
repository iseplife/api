package com.iseplife.api.dao.club.projection;

import com.iseplife.api.constants.ClubRole;

public interface ClubMemberStudentProjection {
  Long getId();
  String getPosition();
  ClubRole getRole();
  ClubPreviewProjection getClub();

  Integer getFromYear();
  Integer getToYear();
}
