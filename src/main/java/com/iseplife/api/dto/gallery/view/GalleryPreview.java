package com.iseplife.api.dto.gallery.view;

import com.iseplife.api.dto.view.EmbedView;
import com.iseplife.api.dto.media.view.ImageView;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class GalleryPreview extends EmbedView {
  private Long id;
  private String name;
  private List<ImageView> preview;
}
