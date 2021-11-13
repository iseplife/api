package com.iseplife.api.dao.subscription.projection;

import org.springframework.beans.factory.annotation.Value;

public interface SubscriptionProjection {
  @Value("#{target.subscription.extensive}")
  boolean isExtensive();
}
