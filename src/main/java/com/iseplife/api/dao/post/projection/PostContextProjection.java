package com.iseplife.api.dao.post.projection;

import com.iseplife.api.constants.FeedType;

public interface PostContextProjection {
    Long getId();
    FeedType getType();
}
