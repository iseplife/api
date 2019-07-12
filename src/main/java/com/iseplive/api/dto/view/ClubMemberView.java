package com.iseplive.api.dto.view;

import com.iseplive.api.constants.ClubRoleEnum;
import com.iseplive.api.entity.club.Club;
import com.iseplive.api.entity.club.ClubRole;
import com.iseplive.api.entity.user.Student;

/**
 * Created by Guillaume on 03/12/2017.
 * back
 */
public class ClubMemberView {
  private Club club;
  private ClubRoleEnum role;
  private Student member;

  public ClubRoleEnum getRole() {
    return role;
  }

  public void setRole(ClubRoleEnum role) {
    this.role = role;
  }

  public Student getMember() {
    return member;
  }

  public void setMember(Student member) {
    this.member = member;
  }

  public Club getClub() {

    return club;
  }

  public void setClub(Club club) {
    this.club = club;
  }
}
