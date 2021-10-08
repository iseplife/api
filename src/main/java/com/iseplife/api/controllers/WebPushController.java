package com.iseplife.api.controllers;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutionException;

import javax.annotation.security.RolesAllowed;

import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iseplife.api.constants.Roles;
import com.iseplife.api.dto.webpush.RegisterPushServiceDTO;
import com.iseplife.api.services.WebPushService;

@RestController
@RequestMapping("/webpush")
public class WebPushController {

  @Autowired
  WebPushService webpushService;

  @PostMapping("register/init")
  @RolesAllowed({ Roles.STUDENT })
  public void registerPushService(@RequestBody RegisterPushServiceDTO register) throws GeneralSecurityException, IOException, JoseException, ExecutionException, InterruptedException {
    webpushService.registerWebPushService(register);
  }
  @PostMapping("register/validate")
  @RolesAllowed({ Roles.STUDENT })
  public void validatePushService(@RequestParam String key) {
    webpushService.validatePushService(key);
  }
}
