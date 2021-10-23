package com.iseplife.api.dto.club;

import com.iseplife.api.constants.ClubRole;
import lombok.Data;

@Data
public class ClubMemberDTO {
  private ClubRole role;
  private String position;
}
