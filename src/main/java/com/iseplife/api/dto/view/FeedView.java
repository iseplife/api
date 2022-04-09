package com.iseplife.api.dto.view;

import com.iseplife.api.constants.FeedType;
import com.iseplife.api.dao.feed.FeedProjection;
import lombok.Data;

@Data
public class FeedView implements FeedProjection {
  private Long id;
  private String name;
  private FeedType type;
}
