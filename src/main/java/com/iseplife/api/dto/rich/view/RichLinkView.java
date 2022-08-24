package com.iseplife.api.dto.rich.view;

import com.iseplife.api.dto.view.EmbedView;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RichLinkView extends EmbedView {
  private Long id;

  private String link;
  private String title;
  private String description;
  private String imageUrl;
}
