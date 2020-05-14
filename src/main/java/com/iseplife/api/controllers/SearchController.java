package com.iseplife.api.controllers;


import com.iseplife.api.constants.Roles;
import com.iseplife.api.dto.view.SearchItemView;
import com.iseplife.api.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

  @Autowired
  SearchService searchService;

  @GetMapping("/student")
  @RolesAllowed({Roles.STUDENT})
  public List<SearchItemView> userSearchPaged(String name, String promos, @RequestParam(defaultValue = "0") Boolean allAnswer) {
    return searchService.searchUserPaged(name, promos, allAnswer);
  }

  @GetMapping("/student/all")
  @RolesAllowed({Roles.STUDENT})
  public List<SearchItemView> userSearch(String name) {
    return searchService.searchUser(name);
  }

  @GetMapping("/club")
  @RolesAllowed({Roles.STUDENT})
  public List<SearchItemView> clubSearch(@RequestParam String filter, @RequestParam(defaultValue = "0") Boolean allAnswer) {
    return searchService.searchClub(filter, allAnswer);
  }

  @GetMapping("/event")
  @RolesAllowed({Roles.STUDENT})
  public List<SearchItemView> eventSearch(@RequestParam String filter, @RequestParam(defaultValue = "0") Boolean allAnswer) {
    return searchService.searchEvent(filter, allAnswer);
  }

}
