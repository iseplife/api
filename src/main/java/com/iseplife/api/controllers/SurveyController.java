package com.iseplife.api.controllers;

import com.iseplife.api.dto.survey.SurveyDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.iseplife.api.constants.Roles;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/survey")
public class SurveyController {

  @PostMapping("/")
  @RolesAllowed({Roles.ADMIN})
  public String test(@RequestBody SurveyDTO dto) {
    return "salut";
  }
}
