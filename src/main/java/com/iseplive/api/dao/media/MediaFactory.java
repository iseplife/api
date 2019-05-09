package com.iseplive.api.dao.media;

import com.iseplive.api.dto.media.VideoEmbedDTO;
import com.iseplive.api.entity.media.VideoEmbed;
import org.springframework.stereotype.Component;

/**
 * Created by Guillaume on 01/08/2017.
 * back
 */
@Component
public class MediaFactory {
  public VideoEmbed dtoToVideoEmbedEntity(VideoEmbedDTO dto) {
    VideoEmbed videoEmbed = new VideoEmbed();
    videoEmbed.setType(dto.getType());
    videoEmbed.setUrl(dto.getUrl());
    return videoEmbed;
  }
}
