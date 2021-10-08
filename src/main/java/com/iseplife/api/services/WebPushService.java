package com.iseplife.api.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.iseplife.api.dao.webpush.WebPushSubscriptionRepository;
import com.iseplife.api.dto.webpush.RegisterPushServiceDTO;
import com.iseplife.api.entity.subscription.WebPushSubscription;
import com.iseplife.api.entity.user.Student;

import net.bytebuddy.utility.RandomString;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;

@Service
public class WebPushService {

  @Autowired
  private StudentService studentService;
  
  @Autowired
  private WebPushSubscriptionRepository webPushSubscriptionRepository;
  
  private Cache<String, WebPushSubscription> pushServiceRegistration = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();

  public void registerWebPushService(RegisterPushServiceDTO sub) throws GeneralSecurityException, IOException, JoseException, ExecutionException, InterruptedException {
    String key = RandomString.make(10);
    
    WebPushSubscription wpsub = new WebPushSubscription();
    wpsub.setAuth(sub.getAuth());
    wpsub.setEndpoint(sub.getEndpoint());
    wpsub.setKey(sub.getKey());
    
    Notification notification = new Notification(wpsub.getEndpoint(), wpsub.getUserPublicKey(), wpsub.getAuthAsBytes(),
        ("{\"type\":\"register\", \"key\":\""+key+"\"}").getBytes(StandardCharsets.UTF_8));

    PushService pushService = new PushService();

    pushService.send(notification);
    
    pushServiceRegistration.put(key, wpsub);
  }
  public void validatePushService(String key) {
    Student student = studentService.getStudent(SecurityService.getLoggedId());
    
    WebPushSubscription sub = pushServiceRegistration.getIfPresent(key);
    pushServiceRegistration.invalidate(key);
    student.addWebPushSubscription(sub);
    
    webPushSubscriptionRepository.save(sub);
    
  }
}
