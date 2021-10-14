package com.iseplife.api.dto.club;

import com.iseplife.api.constants.ClubRole;
import com.iseplife.api.services.ClubService;

public class ClubMemberCreationDTO {
  private Long student;
  private ClubRole role = ClubRole.MEMBER;
  private String position = "Membre";
  private Integer year = ClubService.getCurrentSchoolYear();

  public Long getStudent() {
    return student;
  }

  public void setStudent(Long student) {
    this.student = student;
  }

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

  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    this.year = year;
  }
}
