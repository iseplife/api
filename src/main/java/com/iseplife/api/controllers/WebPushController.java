package com.iseplife.api.controllers;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.annotation.security.RolesAllowed;

import org.jose4j.lang.JoseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
  public void registerPushService(@RequestBody RegisterPushServiceDTO register) {
    try {
      webpushService.registerWebPushService(register);
    }catch(Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something wrong happened when registering your push service");
    }
  }
  @PostMapping("/register/validate")
  public void validatePushService(@RequestBodyParam String key) {
    webpushService.validatePushService(key);
  }
}
