package com.iseplife.api.controllers;

import javax.annotation.security.RolesAllowed;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.subscription.projection.NotificationProjection;
import com.iseplife.api.services.NotificationService;
import com.iseplife.api.services.SecurityService;
import com.iseplife.api.services.StudentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping("/{page}")
  @RolesAllowed({ Roles.STUDENT })
  public Page<NotificationProjection> getNotifications(@PathVariable Integer page) {
    return notificationService.getNotifications(SecurityService.getLoggedId(), page);
  }
  @PostMapping("/watch")
  @RolesAllowed({ Roles.STUDENT })
  public void watchNotifications(@RequestParam(value = "ids[]") Long[] ids) {
    notificationService.setWatched(SecurityService.getLoggedId(), ids);
  }
}
