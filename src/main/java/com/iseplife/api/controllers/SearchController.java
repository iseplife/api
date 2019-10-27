package com.iseplife.api.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class SearchController {

  @GetMapping
  public void globalSearch(@RequestParam String filter){

  }

  @GetMapping("/user")
  public void userSearch(@RequestParam String filter){

  }

  @GetMapping("/club")
  public void clubSearch(@RequestParam String filter){

  }

  @GetMapping("/event")
  public void eventSearch(@RequestParam String filter){

  }

}
