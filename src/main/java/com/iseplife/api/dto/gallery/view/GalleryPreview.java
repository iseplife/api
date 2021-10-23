package com.iseplife.api.dto.gallery.view;

import com.iseplife.api.dto.embed.view.EmbedView;
import com.iseplife.api.dto.embed.view.media.ImageView;
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
