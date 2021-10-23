package com.iseplife.api.dto.media.view;

import com.iseplife.api.dto.view.EmbedView;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class MediaView extends EmbedView {
  protected Long id;
  protected Date creation;
  protected String name;
  protected boolean NSFW;
}
