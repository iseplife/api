package com.iseplife.api.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
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
import nl.martijndwars.webpush.Utils;

@Service
public class WebPushService {

  @Autowired
  private StudentService studentService;
  
  @Autowired
  private WebPushSubscriptionRepository webPushSubscriptionRepository;

  private PublicKey publicKey;
  private PrivateKey privateKey;
  
  public WebPushService() {
    try {
      publicKey = Utils.loadPublicKey("BLWwNN2_bMjIeoh9JDxSVIx2qwrBchWDMHrb6nD1nDijSMoq6ZidqapvWMv5Git2SrObd8Do9glexD9wT-jECnY");
      privateKey = Utils.loadPrivateKey("6WCXXQvA_RxtTBH2i9si-yHu-Kzd36uzHM5CRE68dp4");
    } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeySpecException e) {
      e.printStackTrace();
    }
  }
  
  
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
    pushService.setPublicKey(publicKey);
    pushService.setPrivateKey(privateKey);
    System.out.println("doo");
    pushService.send(notification);
    System.out.println("done");
    
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
