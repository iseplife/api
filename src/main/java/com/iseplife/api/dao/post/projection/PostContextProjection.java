package com.iseplife.api.dao.post.projection;

import org.springframework.beans.factory.annotation.Value;

import com.iseplife.api.constants.FeedType;

public interface PostContextProjection {
    @Value("#{"
        + "target.type == T(com.iseplife.api.constants.FeedType).GROUP ? target.group.id : "
        + "target.type == T(com.iseplife.api.constants.FeedType).EVENT ? target.event.id : "
        + "target.type == T(com.iseplife.api.constants.FeedType).STUDENT ? target.student.id : "
        + "target.type == T(com.iseplife.api.constants.FeedType).CLUB ? target.club.id : "
        + "null}")
    Long getId();
    FeedType getType();
    String getName();
}
