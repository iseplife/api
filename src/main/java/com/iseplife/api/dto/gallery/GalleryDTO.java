package com.iseplife.api.dto.gallery;

import lombok.Data;

import java.util.List;


@Data
public class GalleryDTO {
  private String name;
  private String description;
  private boolean pseudo = true;
  private Boolean generatePost = false;
  private List<Long> images;
  private Long club;
  private Long feed;
}
