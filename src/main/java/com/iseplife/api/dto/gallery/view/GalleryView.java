package com.iseplife.api.dto.gallery.view;

import com.iseplife.api.dto.club.view.ClubPreview;
import com.iseplife.api.dto.media.view.ImageView;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GalleryView {
  private Long id;
  private String name;
  private Date creation;
  private List<ImageView> images;
  private ClubPreview club;
  private Boolean hasRight;
}
