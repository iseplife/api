package com.iseplife.api.dto.club;

import com.iseplife.api.constants.ClubRole;

public class ClubMemberDTO {
  private ClubRole role;
  private String position;

  public ClubRole getRole() {
    return role;
  }

  public void setRole(ClubRole role) {
    this.role = role;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }
}
