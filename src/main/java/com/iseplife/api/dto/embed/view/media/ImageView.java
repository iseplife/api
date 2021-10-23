package com.iseplife.api.dto.embed.view.media;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ImageView extends MediaView {
  private Long thread;
}
