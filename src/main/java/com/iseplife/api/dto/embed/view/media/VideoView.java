package com.iseplife.api.dto.embed.view.media;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VideoView extends MediaView {
  private String title;
  private Integer views;
}
