package com.iseplife.api.dto.media.view;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ImageView extends MediaView {
  private Long thread;
}
