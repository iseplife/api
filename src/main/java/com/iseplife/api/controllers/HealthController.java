package com.iseplife.api.controllers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iseplife.api.dto.app.AppUpdateResponse;

import net.minidev.json.JSONObject;

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

  @Value("${app.fronturl}")
  String frontUrl;
  
  String latestVersion = "";
  long latestVersionFetch = 0;
  
  private String getLatestVersion() {
    long now = System.currentTimeMillis();
    if(now-latestVersionFetch > 15000) {
      try {
        String oldVersion = latestVersion;
        latestVersion = Jsoup.connect(frontUrl+"/version.txt").ignoreContentType(true).get().text();
        if(!oldVersion.equals(latestVersion))
          System.out.println("New latest version is "+latestVersion);
      } catch (IOException e) {
        e.printStackTrace();
      }
      latestVersionFetch = now;
    }
    return latestVersion;
  }

  @GetMapping("/update")
  public Object getUpdate(@RequestHeader("cap_version_name") String versionName){
    String version = getLatestVersion();
    if(!version.equals(versionName))
      return new AppUpdateResponse(version, frontUrl+"/app.zip");
    
    return new JSONObject();
  }
  @GetMapping("/updatee")
  public Object getUpdatee(@RequestHeader("cap_version_name") String versionName){
    //i'm an idiot
    String version = getLatestVersion();
    if(!version.equals(versionName))
      return new AppUpdateResponse(version, frontUrl+"/app.zip");
    
    return new JSONObject();
  }
}
