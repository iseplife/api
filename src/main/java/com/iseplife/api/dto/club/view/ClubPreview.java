package com.iseplife.api.dto.club.view;

import com.iseplife.api.dao.club.projection.ClubPreviewProjection;

import lombok.Data;

@Data
public class ClubPreview implements ClubPreviewProjection {
  private Long id;
  private String name;
  private String description;
  private String logoUrl;
}
