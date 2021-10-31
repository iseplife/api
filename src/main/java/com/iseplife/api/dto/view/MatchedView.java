package com.iseplife.api.dto.view;

import com.iseplife.api.entity.post.embed.media.Image;
import com.iseplife.api.entity.user.Student;
import lombok.Data;

@Data
public class MatchedView {
  private Long id;
  private Image image;
  private Student owner;
  private Long galleryId;
}
