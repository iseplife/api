package com.iseplife.api.controllers;

import javax.annotation.security.RolesAllowed;
import org.springframework.web.bind.annotation.*;

import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.webpush.WebPushValidateServiceDTO;
import com.iseplife.api.dto.webpush.RegisterPushServiceDTO;
import com.iseplife.api.exceptions.http.HttpInternalServerErrorException;
import com.iseplife.api.services.WebPushService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/webpush")
public class WebPushController {

  private final WebPushService webpushService;

  @PostMapping("/register/init")
  @RolesAllowed({ Roles.STUDENT })
  public void registerPushService(@RequestBody RegisterPushServiceDTO register) {
    try {
      webpushService.registerWebPushService(register);
    }catch(Exception e) {
      throw new HttpInternalServerErrorException("web_push_init_failed");
    }
  }
  @PostMapping("/register/validate")
  public void validatePushService(@RequestBody WebPushValidateServiceDTO body) {
    webpushService.validatePushService(body.getKey());
  }
}
