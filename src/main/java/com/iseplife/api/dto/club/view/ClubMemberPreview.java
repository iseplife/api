package com.iseplife.api.dto.club.view;

import lombok.Data;

@Data
public class ClubMemberPreview {
  private Long id;
  private String position;
  private ClubPreview club;
}
