package com.iseplife.api.dto.embed.view.media;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DocumentView extends MediaView{
  private String title;
}
