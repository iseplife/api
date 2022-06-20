package com.iseplife.api.dto.media.view;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VideoView extends MediaView {
  private String title;
  private Integer views;
  private Double ratio;
}
