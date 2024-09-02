package com.iseplife.api.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.AndroidNotification.Priority;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.iseplife.api.constants.Language;
import com.iseplife.api.dao.firebase.FirebaseSubscriptionRepository;
import com.iseplife.api.dto.webpush.RegisterPushServiceDTO;
import com.iseplife.api.entity.subscription.FirebaseSubscription;
import com.iseplife.api.entity.subscription.Subscription;
import com.iseplife.api.entity.user.Student;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FirebaseMessengerService {
  @Lazy private final StudentService studentService;
  private final FirebaseSubscriptionRepository firebaseSubscriptionRepository;
  private final NotificationTranslationService translationService;
  
  @Value("${storage.url}")
  private String storageUrl;
  
  static {
    if(FirebaseApp.getApps().size() == 0){
      FirebaseOptions options;
      try {
        FileInputStream serviceAccount = new FileInputStream("firebase-auth.json");
        options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();

        FirebaseApp.initializeApp(options);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  private Cache<String, FirebaseSubscription> pushServiceRegistration = CacheBuilder
    .newBuilder()
    .expireAfterWrite(10, TimeUnit.MINUTES)
    .build();

  public void registerWebPushService(RegisterPushServiceDTO sub)
      throws GeneralSecurityException, IOException, JoseException, ExecutionException, InterruptedException, TimeoutException {
    Long loggedId = SecurityService.getLoggedId();
    Student student = studentService.getStudent(loggedId);
    try {
      FirebaseMessaging.getInstance().subscribeToTopic(Arrays.asList(sub.getSubscriptionKey()), "s"+student.getId());
      Optional<FirebaseSubscription> optional = firebaseSubscriptionRepository.findFirstByTokenOrFingerprintOrderByLastUpdate(sub.getSubscriptionKey(), sub.getFingerprint());
      FirebaseSubscription subscription;
      if(optional.isPresent())
        subscription = optional.get();
      else 
        subscription = new FirebaseSubscription();
      
      subscription.setFingerprint(sub.getFingerprint());
      subscription.setToken(sub.getSubscriptionKey());
      subscription.setOwner(student);
      
      firebaseSubscriptionRepository.save(subscription);
      System.out.println("Subscription updated");
    } catch (FirebaseMessagingException e) {
      e.printStackTrace();
    }
  }

  private List<String> getTokens(Student student) {
    var subs = student.getFirebaseSubscriptions();

    //enable in 3 days
    if(System.currentTimeMillis() < 1725033452513L + 1000*60*60*24*3) {
      return subs.stream().map(s -> s.getToken()).collect(Collectors.toList());
    }
    var goodSubs = subs
      .stream()
      .filter(s -> s.getLastUpdate() != null && s.getLastUpdate().after(new Date(System.currentTimeMillis() - 1000*60*60*24*30*3)))
      .collect(Collectors.toList());
      
    return (
        goodSubs.size() >= 1 ? 
          goodSubs
        :
          subs
      ).stream().map(s -> s.getToken()).collect(Collectors.toList());
  }

  public void sendNotificationToAll(Iterable<Student> subs, com.iseplife.api.entity.subscription.Notification notification) {
    HashMap<Language, List<String>> tokens = new HashMap<>();
    
    for(Student sub : subs) {
      List<String> list = tokens.get(sub.getLanguage());
      
      if(list == null)
        tokens.put(sub.getLanguage(), list = new ArrayList<>());

      list.addAll(getTokens(sub));
    }
    
    String image = mediaPath(notification.getIcon(), "300x300");
    int sent = 0;
    for(Entry<Language, List<String>> entry : tokens.entrySet()) {
      String body = translationService.getTranslation(notification.getType(), notification.getInformations(), entry.getKey());
      int max = entry.getValue().size();
      for(int i = 0;i<max;i+=500) {
        List<String> list = entry.getValue().subList(i, Math.min(i+500, max));
        MulticastMessage message = MulticastMessage.builder().addAllTokens(list).setNotification(
            Notification.builder()
              .setBody(body)
              .setImage(image)
              .setTitle("iseplife")
              .build()
            ).setAndroidConfig(AndroidConfig.builder().setNotification(
              AndroidNotification.builder()
                .setPriority(Priority.HIGH)
                .setBody(body)
                .setImage(image)
                .setTitle("iseplife")
                .build()
            ).build())
            .putData("link", notification.getLink())
          .build();
        
        sent += entry.getValue().size();
        
        FirebaseMessaging.getInstance().sendMulticastAsync(message);
      }
    }
    
    System.out.println("Notification sent to "+sent+" users");
    
  }
  private String mediaPath(String fullPath, String size) {
    if (fullPath != null) {
      if (size != null) {
        String[] splitted = fullPath.split("/");
        StringJoiner path = new StringJoiner("/");
        for(int i = 0;i<splitted.length-1;i++)
          path.add(splitted[i]);
        fullPath = path+"/"+size+"/"+splitted[splitted.length - 1];
      }
  
      return storageUrl+fullPath;
    }
    return fullPath;
  }
  public void validatePushService(String key) {
  /*  WebPushSubscription sub = pushServiceRegistration.getIfPresent(key);
    if(sub == null)
      return;
    pushServiceRegistration.invalidate(key);
    webPushSubscriptionRepository.save(sub);*/
  }
}
