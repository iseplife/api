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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.asynchttpclient.Response;
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
import nl.martijndwars.webpush.PushAsyncService;
import nl.martijndwars.webpush.Utils;

@Service
public class WebPushService {

  @Autowired
  private StudentService studentService;

  @Autowired
  private WebPushSubscriptionRepository webPushSubscriptionRepository;

  private PublicKey publicKey;
  private PrivateKey privateKey;
  
  private PushAsyncService pushService;

  public WebPushService() {
    try {
      publicKey = Utils
          .loadPublicKey("BLWwNN2_bMjIeoh9JDxSVIx2qwrBchWDMHrb6nD1nDijSMoq6ZidqapvWMv5Git2SrObd8Do9glexD9wT-jECnY");
      privateKey = Utils.loadPrivateKey("6WCXXQvA_RxtTBH2i9si-yHu-Kzd36uzHM5CRE68dp4");
    } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeySpecException e) {
      e.printStackTrace();
    }
    pushService = new PushAsyncService();
    pushService.setPublicKey(publicKey);
    pushService.setPrivateKey(privateKey);
  }
  
  private Cache<String, WebPushSubscription> pushServiceRegistration = CacheBuilder.newBuilder()
      .expireAfterWrite(10, TimeUnit.MINUTES).build();

  public void registerWebPushService(RegisterPushServiceDTO sub)
      throws GeneralSecurityException, IOException, JoseException, ExecutionException, InterruptedException, TimeoutException {
    String key = RandomString.make(32);

    Long loggedId = SecurityService.getLoggedId();

    WebPushSubscription wpsub = webPushSubscriptionRepository.findByAuthAndKeyAndEndpointAndOwner_IdOrFingerprint(
        sub.getAuth(), sub.getKey(), sub.getEndpoint(), loggedId, sub.getFingerprint()).orElse(null);
    if (wpsub != null) {
      breaking: {
        boolean sameSubscription = sub.getAuth().equals(wpsub.getAuth()) && sub.getEndpoint().equals(wpsub.getEndpoint());
        if (!sameSubscription || !sub.getFingerprint().equals(wpsub.getFingerprint())) {
          wpsub.setFingerprint(sub.getFingerprint());
          wpsub.setLastUpdate(new Date());

          if(sameSubscription)
            webPushSubscriptionRepository.save(wpsub);
          else
            break breaking;
        } else
          webPushSubscriptionRepository.updateDate(wpsub.getId(), new Date());
        
        return;
      }
    }
    
    Student student = studentService.getStudent(loggedId);
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

  public void validatePushService(String key) {
    WebPushSubscription sub = pushServiceRegistration.getIfPresent(key);
    pushServiceRegistration.invalidate(key);
    sub.getOwner().getWebPushSubscriptions().add(sub);

    webPushSubscriptionRepository.save(sub);
  }
}
