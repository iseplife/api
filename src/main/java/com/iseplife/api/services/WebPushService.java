package com.iseplife.api.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.asynchttpclient.Response;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.iseplife.api.dao.webpush.WebPushSubscriptionRepository;
import com.iseplife.api.dto.webpush.RegisterPushServiceDTO;
import com.iseplife.api.entity.subscription.Subscription;
import com.iseplife.api.entity.subscription.WebPushSubscription;
import com.iseplife.api.entity.user.Student;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushAsyncService;
import nl.martijndwars.webpush.Utils;

@Service
@RequiredArgsConstructor
public class WebPushService {
  @Lazy private final StudentService studentService;
  private final WebPushSubscriptionRepository webPushSubscriptionRepository;
  private final NotificationTranslationService translationService;

  private PublicKey publicKey;
  private PrivateKey privateKey;

  @Value("${push.web.private-key}")
  private String privateKeyStr;
  @Value("${push.web.public-key}")
  private String publicKeyStr;

  private PushAsyncService pushService;

  @PostConstruct
  public void init() {
    try {
      publicKey = Utils.loadPublicKey(publicKeyStr);
      privateKey = Utils.loadPrivateKey(privateKeyStr);
    } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeySpecException e) {
      e.printStackTrace();
    }
    pushService = new PushAsyncService();
    pushService.setPublicKey(publicKey);
    pushService.setPrivateKey(privateKey);
  }

  private Cache<String, WebPushSubscription> pushServiceRegistration = CacheBuilder
    .newBuilder()
    .expireAfterWrite(10, TimeUnit.MINUTES)
    .build();

  public void registerWebPushService(RegisterPushServiceDTO sub)
      throws GeneralSecurityException, IOException, JoseException, ExecutionException, InterruptedException, TimeoutException {
    String key = RandomString.make(32);

    Long loggedId = SecurityService.getLoggedId();
    Student student = studentService.getStudent(loggedId);

    WebPushSubscription wpsub = webPushSubscriptionRepository.findByAuthAndKeyAndEndpointOrFingerprint(
        sub.getAuth(), sub.getKey(), sub.getEndpoint(), sub.getFingerprint()).orElse(null);
   
    if(wpsub != null)
      breaking: {
        boolean sameSubscription = sub.getAuth().equals(wpsub.getAuth()) && sub.getEndpoint().equals(wpsub.getEndpoint());
        boolean sameFingerprint = sub.getFingerprint().equals(wpsub.getFingerprint());
        
        if (!sameSubscription || !sameFingerprint) {
          wpsub.setFingerprint(sub.getFingerprint());
          wpsub.setLastUpdate(new Date());
          
          wpsub.setKey(sub.getKey());
          
          if(!sameSubscription) {
            webPushSubscriptionRepository.delete(wpsub);
            break breaking;
          }
          
          webPushSubscriptionRepository.save(wpsub);
        } else
          webPushSubscriptionRepository.updateDateAndOwner(wpsub.getId(), new Date(), student);
        return;
      }

    if(wpsub == null)
      wpsub = new WebPushSubscription();
    wpsub.setAuth(sub.getAuth());
    wpsub.setEndpoint(sub.getEndpoint());
    wpsub.setKey(sub.getKey());
    wpsub.setOwner(student);
    wpsub.setFingerprint(sub.getFingerprint());

    sendSyncNotification(new Notification(wpsub.getEndpoint(), wpsub.getUserPublicKey(), wpsub.getAuthAsBytes(),
        ("{\"type\":\"register\", \"key\":\"" + key + "\"}").getBytes(StandardCharsets.UTF_8)));

    pushServiceRegistration.put(key, wpsub);
  }

  private void sendSyncNotification(Notification notification)
      throws GeneralSecurityException, IOException, JoseException, ExecutionException, InterruptedException, TimeoutException {
    int code = sendAsyncNotification(notification).get(10, TimeUnit.SECONDS).getStatusCode();
    if(code != 201)
      throw new IllegalArgumentException("bad_subscription");
  }
  private CompletableFuture<Response> sendAsyncNotification(Notification notification) throws GeneralSecurityException, IOException, JoseException {
    return pushService.send(notification);
  }

  public void sendAsyncNotification(WebPushSubscription sub, String payload) {
    try {
      pushService.send(new Notification(sub.getEndpoint(), sub.getKey(), sub.getAuth(), payload)).thenAccept(response -> {
        if(response.getStatusCode() != 201)
          webPushSubscriptionRepository.delete(sub);
      }).exceptionally(e->{
        System.out.println("Error on exceptionally, webpush sub deleted !");
        webPushSubscriptionRepository.delete(sub);
        return null;
      });
    } catch (GeneralSecurityException | IOException | JoseException e) {
      System.out.println("Error, webpush sub deleted !");
      webPushSubscriptionRepository.delete(sub);
    }
  }

  public void sendNotificationToAll(List<Subscription> subs, com.iseplife.api.entity.subscription.Notification notification) {
    new Thread(() -> this.sendNotificationToAllSync(subs, notification)).start();
  }
  
  private void sendNotificationToAllSync(List<Subscription> subs, com.iseplife.api.entity.subscription.Notification notification) {
    try {
      for(Subscription sub : subs) {
        String payload = notification.getPayload(sub.getListener(), translationService);
        for(WebPushSubscription wpSub : sub.getListener().getWebPushSubscriptions()) {
          sendAsyncNotification(wpSub, payload);
          Thread.sleep(5);
        }
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void validatePushService(String key) {
    WebPushSubscription sub = pushServiceRegistration.getIfPresent(key);
    if(sub == null)
      return;
    pushServiceRegistration.invalidate(key);
    webPushSubscriptionRepository.save(sub);
  }
}
