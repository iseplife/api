package com.iseplife.api.dto.gallery.view;

import com.iseplife.api.dto.view.EmbedView;
import com.iseplife.api.dto.media.view.ImageView;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PseudoGalleryView extends EmbedView {
  private Long id;
  private List<ImageView> images;
}
