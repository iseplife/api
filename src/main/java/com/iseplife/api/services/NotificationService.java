package com.iseplife.api.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.iseplife.api.dao.subscription.NotificationRepository;
import com.iseplife.api.dao.subscription.SubscriptionRepository;
import com.iseplife.api.dao.subscription.projection.NotificationProjection;
import com.iseplife.api.entity.subscription.Notification;
import com.iseplife.api.entity.subscription.Subscribable;
import com.iseplife.api.entity.subscription.Subscription;
import com.iseplife.api.entity.user.Student;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
  @Lazy private final WebPushService webPushService;
  private final SubscriptionRepository subscriptionRepository;
  private final NotificationRepository notificationRepository;

  private static int NOTIFICATIONS_PER_PAGE = 10;

  public void delayNotification(Notification notif, boolean extensive, Subscribable subable, DelayedNotificationCheck check) {
    delayNotification(notif, extensive, subable, check, null);
  }
  public void delayNotification(Notification notif, boolean extensive, Subscribable subable, DelayedNotificationCheck check, StudentValidationCallback studentValidator) {
    Timer timer = new Timer();
    //We wait 10s so that we don't send a notification for an aborted event.
    timer.schedule(new TimerTask() {

      @Override
      public void run() {
        if(check.isNotificationStillUseful()) {
          Set<Student> notified = new HashSet<>();
          List<Subscription> subs = subscriptionRepository.findBySubscribed(subable);

          if(studentValidator != null)
            subs = Arrays.asList(subs.stream().filter(sub -> studentValidator.validate(sub.getListener())).toArray(i -> new Subscription[i]));

          if(extensive)
            subs = Arrays.asList(subs.stream().filter(sub -> sub.isExtensive()).toArray(i-> new Subscription[i]));

          subs.forEach(s -> notified.add(s.getListener()));

          notif.setStudents(new ArrayList<Student>(notified));

          notificationRepository.save(notif);

          webPushService.sendNotificationToAll(subs, notif.getPayload());
        }
      }
    }, 1000 * 10);
  }

  public Page<NotificationProjection> getNotifications(Student student, int page) {
    return notificationRepository.findAllByStudentsOrderById(student, PageRequest.of(page, NOTIFICATIONS_PER_PAGE));
  }
  public Page<NotificationProjection> getNotifications(Long student, int page) {
    return notificationRepository.findAllByStudentsIdOrderById(student, PageRequest.of(page, NOTIFICATIONS_PER_PAGE));
  }
  public long countUnwatchedNotifications(Student student) {
    return notificationRepository.countUnwatchedByStudents(student.getId());
  }
  public long countUnwatchedNotifications(Long student) {
    return notificationRepository.countUnwatchedByStudents(student);
  }
  public void setWatched(Long student, Long ids[]) {
    notificationRepository.setWatched(student, ids);
  }


  public static interface DelayedNotificationCheck {
    public boolean isNotificationStillUseful();
  }
  public static interface StudentValidationCallback {
    public boolean validate(Student student);
  }
}
