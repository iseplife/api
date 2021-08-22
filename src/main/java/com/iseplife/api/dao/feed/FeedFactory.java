package com.iseplife.api.dao.feed;

import com.iseplife.api.dto.view.FeedView;
import com.iseplife.api.entity.feed.Feed;

public class FeedFactory {

  public static FeedView toView(Feed feed){
    FeedView view = new FeedView();

    view.setId(feed.getId());
    view.setName(feed.getName());

    return view;
  }
}
