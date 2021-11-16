package com.iseplife.api.controllers;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.annotation.security.RolesAllowed;

import org.jose4j.lang.JoseException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.lambdaexpression.annotation.EnableRequestBodyParam;
import com.github.lambdaexpression.annotation.RequestBodyParam;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dto.webpush.RegisterPushServiceDTO;
import com.iseplife.api.services.WebPushService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@EnableRequestBodyParam
@RequestMapping("/webpush")
public class WebPushController {

  private final WebPushService webpushService;
  
  @PostMapping("/register/init")
  @RolesAllowed({ Roles.STUDENT })
  public void registerPushService(@RequestBody RegisterPushServiceDTO register) throws GeneralSecurityException, IOException, JoseException, ExecutionException, InterruptedException, TimeoutException {
    webpushService.registerWebPushService(register);
  }
  @PostMapping("/register/validate")
  public void validatePushService(@RequestBodyParam String key) {
    webpushService.validatePushService(key);
  }
}
