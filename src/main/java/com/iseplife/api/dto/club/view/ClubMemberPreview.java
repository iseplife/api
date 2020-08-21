package com.iseplife.api.dto.club.view;

public class ClubMemberPreview {
  private Long id;
  private String position;
  private ClubPreview club;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public ClubPreview getClub() {
    return club;
  }

  public void setClub(ClubPreview club) {
    this.club = club;
  }
}
