package com.iseplife.api.services;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.stereotype.Service;

import com.iseplife.api.dao.subscription.SubscriptionRepository;
import com.iseplife.api.entity.subscription.Notification;
import com.iseplife.api.entity.subscription.Subscribable;
import com.iseplife.api.entity.subscription.Subscription;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
  private final SubscriptionRepository subscriptionRepository;
  private final WebPushService webPushService;
  
  public void delayNotification(Notification notif, Subscribable subable, DelayedNotificationCheck check) {
    Timer timer = new Timer();
    //We wait 10s so that we don't send a notification for an aborted event.
    timer.schedule(new TimerTask() {
      
      @Override
      public void run() {
        if(check.isNotificationStillUseful()) {
          List<Subscription> subs = subscriptionRepository.findBySubscribed(subable);
          webPushService.sendNotificationToAll(subs, notif.getPayload());
        }
      }
    }, 1000 * 10);
  }
  
  public static interface DelayedNotificationCheck {
    public boolean isNotificationStillUseful();
  }
}
