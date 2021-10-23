package com.iseplife.api.dto.embed.view;

import com.iseplife.api.entity.post.embed.Embedable;
import lombok.Data;

@Data
public abstract class EmbedView implements Embedable {
  String embedType;
}
