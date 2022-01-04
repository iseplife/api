package com.iseplife.api.dto.thread.view;

public interface ThreadProjection {
  Long getId();
  Integer getNbLikes();
  Integer getNbComments();
  boolean getLiked();
}
