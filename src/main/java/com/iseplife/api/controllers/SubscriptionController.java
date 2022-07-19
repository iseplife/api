package com.iseplife.api.controllers;

import javax.annotation.security.RolesAllowed;

import org.springframework.web.bind.annotation.*;

import com.iseplife.api.constants.Roles;
import com.iseplife.api.dto.subscription.SubscribeDTO;
import com.iseplife.api.entity.subscription.Subscribable;
import com.iseplife.api.entity.subscription.Subscription;
import com.iseplife.api.exceptions.http.HttpForbiddenException;
import com.iseplife.api.services.SecurityService;
import com.iseplife.api.services.SubscriptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscription")
public class SubscriptionController {

  private final SubscriptionService subscriptionService;

  @PutMapping("/{type}/{id}")
  @RolesAllowed({ Roles.STUDENT })
  public void addSubscription(@PathVariable String type, @PathVariable Long id, @RequestBody SubscribeDTO body) {
    if(SecurityService.getLoggedId() == id)
      throw new HttpForbiddenException("cant_sub_self");
    
    Subscription sub = subscriptionService.getSubscription(id);
    if(sub != null) {
      if(sub.isExtensive() != body.isExtensive()) {
        sub.setExtensive(body.isExtensive());
        subscriptionService.updateSubscription(sub);
      }
      return;
    }

    Subscribable subbing = subscriptionService.getSubscribable(type, id);

    subscriptionService.subscribe(subbing, body.isExtensive());
  }

  @DeleteMapping("/{id}")
  @RolesAllowed({ Roles.STUDENT })
  public void removeSubscription(@PathVariable Long id) {
    if(SecurityService.getLoggedId() == id)
      throw new HttpForbiddenException("cant_sub_self");
    
    subscriptionService.unsubscribe(id);
  }

  @GetMapping("/{id}")
  @RolesAllowed({ Roles.STUDENT })
  public Boolean isSubscribed(@PathVariable Long id) {
    return subscriptionService.isSubscribed(id);
  }
}
