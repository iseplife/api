package com.iseplife.api.dao.poll;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

public interface PollProjection {
  Long getId();
  boolean isMultiple();
  boolean isAnonymous();
  Date getCreation();
  Date getEndsAt();
  @Value("#{new java.util.ArrayList()}")
  List<PollChoiceProjection> getChoices();
}
