package com.iseplife.api.controllers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/dd")
public class DataDogForward {

  @PostMapping()
  public String proxy(@RequestParam("ddforward") String ddforward, @RequestParam("segment") MultipartFile segment, @RequestParam("event") MultipartFile event){
    // Forward request to datadog
    System.out.println("Forwarding to datadog "+ddforward.substring(1));
    try {
      return Jsoup.connect("https://browser-intake-datadoghq.com/"+ddforward.substring(1))
        .data("segment", segment.getOriginalFilename(), segment.getInputStream())
        .data("event", event.getOriginalFilename(), event.getInputStream())
        .post().body().text();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "";
  }

}
