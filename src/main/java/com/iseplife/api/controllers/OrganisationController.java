package com.iseplife.api.controllers;

import com.iseplife.api.constants.Roles;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/organisation")
public class OrganisationController {

  @PostMapping("/free-room")
  @RolesAllowed({Roles.STUDENT})
  public void getRoomsAvailable(){

  }

  @PostMapping("/planning-matcher")
  @RolesAllowed({Roles.STUDENT})
  public void getAvailableSlots(){

  }

}
