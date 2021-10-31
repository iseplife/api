package com.iseplife.api.dto.club.view;

import com.iseplife.api.dao.club.projection.ClubMemberProjection;
import com.iseplife.api.dto.student.view.StudentPreview;
import com.iseplife.api.constants.ClubRole;
import lombok.Data;


@Data
public class ClubMemberView implements ClubMemberProjection {
  private Long id;
  private String position;
  private ClubRole role;
  private StudentPreview student;
  private Long parent;
}
