package com.iseplife.api.dto.post.view;

import com.iseplife.api.constants.FeedType;
import com.iseplife.api.dao.post.projection.PostContextProjection;
import com.iseplife.api.entity.feed.Feed;
import lombok.Data;

@Data
public class PostContextView implements PostContextProjection {
  private Long id;
  private FeedType type;
  private String name;
  private Long feedId;

  public PostContextView(Feed feed){
    id = feed.getFeedContext().getId();
    type = feed.getType();
    name = feed.getName();
    feedId = feed.getId();
  }
}
