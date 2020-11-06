package com.iseplife.api.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

  @Value("${app.version}")
  String version;

  @GetMapping
  public String getStatus(){
    return "Ok";
  }

  @GetMapping("/version")
  public String getVersion(){
    return version;
  }
}
