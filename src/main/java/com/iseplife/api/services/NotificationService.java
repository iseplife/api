package com.iseplife.api.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.iseplife.api.entity.subscription.Notification.NotificationBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.iseplife.api.dao.subscription.NotificationRepository;
import com.iseplife.api.dao.subscription.SubscriptionRepository;
import com.iseplife.api.dao.subscription.projection.NotificationCountProjection;
import com.iseplife.api.dao.subscription.projection.NotificationProjection;
import com.iseplife.api.entity.subscription.Notification;
import com.iseplife.api.entity.subscription.Subscribable;
import com.iseplife.api.entity.subscription.Subscription;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.websocket.services.WSNotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
  @Lazy private final FirebaseMessengerService webPushService;
  @Lazy private final WSNotificationService wsNotifService;
  private final SubscriptionRepository subscriptionRepository;
  private final NotificationRepository notificationRepository;

  private final static int NOTIFICATIONS_PER_PAGE = 15;

  public void delayNotification(NotificationBuilder builder, boolean extensive, Subscribable subable, DelayedNotificationCheck check) {
    delayNotification(builder, extensive, subable, check, null);
  }
  public void delayNotification(NotificationBuilder builder, boolean extensive, Subscribable subable, DelayedNotificationCheck check, StudentValidationCallback studentValidator) {
    Timer timer = new Timer();
    //We wait 10s so that we don't send a notification for an aborted event.
    timer.schedule(new TimerTask() {

      @Override
      public void run() {
        if(check.isNotificationStillUseful()) {
          Notification notif = builder.build();
          Set<Student> notified = new HashSet<>();
          List<Subscription> subs = subscriptionRepository.findBySubscribed(subable);

          if(studentValidator != null)
            subs = Arrays.asList(
              subs.stream()
                .filter(sub -> studentValidator.validate(sub.getListener()))
                .toArray(Subscription[]::new)
            );

          if(extensive)
            subs = Arrays.asList(
              subs.stream()
                .filter(Subscription::isExtensive)
                .toArray(Subscription[]::new)
            );

          subs.forEach(s -> notified.add(s.getListener()));
          notif.setStudents(new ArrayList<>(notified));

          notif = notificationRepository.save(notif);

          wsNotifService.broadcastNotification(getUnwatchedNotificationProjection(notif.getId()), notified);
          webPushService.sendNotificationToAll(subs, notif);
        }
      }
    }, 1000 * 10);
  }


  public Page<NotificationProjection> getNotifications(Long student, int page) {
    return notificationRepository.findAllByStudentsIdOrderById(
      student,
      PageRequest.of(
        page,
        NOTIFICATIONS_PER_PAGE,
        Sort.by(Sort.Direction.DESC, "creation"))
    );
  }
  public NotificationProjection getUnwatchedNotificationProjection(Long id) {
    return notificationRepository.findUnwatchedProjectionById(id);
  }

  public long countUnwatchedNotifications(Student student) {
    return notificationRepository.countUnwatchedByStudents(student.getId());
  }

  public NotificationCountProjection countUnwatchedAndAllByStudents(Student student) {
    return notificationRepository.countUnwatchedAndAllByStudents(student.getId());
  }

  public long countUnwatchedNotifications(Long student) {
    return notificationRepository.countUnwatchedByStudents(student);
  }

  public void setWatched(Long student, Long ids[]) {
    notificationRepository.setWatched(student, ids);
  }


  public static interface DelayedNotificationCheck {
    boolean isNotificationStillUseful();
  }
  public static interface StudentValidationCallback {
    boolean validate(Student student);
  }
}
