package com.iseplife.api.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.stereotype.Service;

import com.iseplife.api.dao.subscription.NotificationRepository;
import com.iseplife.api.dao.subscription.SubscriptionRepository;
import com.iseplife.api.entity.subscription.Notification;
import com.iseplife.api.entity.subscription.Subscribable;
import com.iseplife.api.entity.subscription.Subscription;
import com.iseplife.api.entity.user.Student;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
  private final SubscriptionRepository subscriptionRepository;
  private final NotificationRepository notificationRepository;
  private final WebPushService webPushService;
  
  public void delayNotification(Notification notif, boolean extensive, Subscribable subable, DelayedNotificationCheck check) {
    Timer timer = new Timer();
    //We wait 10s so that we don't send a notification for an aborted event.
    timer.schedule(new TimerTask() {
      
      @Override
      public void run() {
        if(check.isNotificationStillUseful()) {
          Set<Student> notified = new HashSet<>();
          List<Subscription> subs = subscriptionRepository.findBySubscribed(subable);
          if(extensive)
            subs = Arrays.asList(subs.stream().filter(sub -> sub.isExtensiveSubscription()).toArray(i->new Subscription[i]));
          
          subs.forEach(s -> notified.add(s.getListener()));
          
          notif.setStudents(new ArrayList<Student>(notified));
          
          notificationRepository.save(notif);
          
          webPushService.sendNotificationToAll(subs, notif.getPayload());
        }
      }
    }, 1000 * 10);
  }
  
  public static interface DelayedNotificationCheck {
    public boolean isNotificationStillUseful();
  }
}
