package com.iseplife.api.dto.club;

import com.iseplife.api.constants.ClubRole;
import com.iseplife.api.services.ClubService;
import lombok.Data;

@Data
public class ClubMemberCreationDTO {
  private Long student;
  private ClubRole role = ClubRole.MEMBER;
  private String position = "Membre";
  private Integer year = ClubService.getCurrentSchoolYear();
}
