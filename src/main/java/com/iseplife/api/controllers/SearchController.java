package com.iseplife.api.controllers;

import com.iseplife.api.constants.Roles;
import com.iseplife.api.dto.view.SearchItemView;
import com.iseplife.api.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

  @GetMapping
  @RolesAllowed({Roles.STUDENT})
  public Page<SearchItemView> globalSearch(@RequestParam String name, @RequestParam(defaultValue = "0") Integer page,
          @RequestParam(defaultValue = "0") Boolean allAnswer) {
    return searchService.globalSearch(name, page, allAnswer);
  }

  @GetMapping("/student")
  @RolesAllowed({Roles.STUDENT})
  public Page<SearchItemView> userSearch(String name, String promos, Integer page, Boolean atoz) {
    return searchService.searchUser(name, promos, atoz, page);
  }

  @GetMapping("/student/all")
  @RolesAllowed({Roles.STUDENT})
  public List<SearchItemView> userSearchAll(String name) {
    return searchService.searchUserAll(name);
  }

  @GetMapping("/club")
  @RolesAllowed({Roles.STUDENT})
  public Page<SearchItemView> clubSearch(@RequestParam String name, @RequestParam(defaultValue = "0") Integer page,
          @RequestParam(defaultValue = "0") Boolean allAnswer) {
    return searchService.searchClub(name, page, allAnswer);
  }

  @GetMapping("/event")
  @RolesAllowed({Roles.STUDENT})
  public Page<SearchItemView> eventSearch(@RequestParam String name, @RequestParam(defaultValue = "0") Integer page
          , @RequestParam(defaultValue = "0") Boolean allAnswer) {
    return searchService.searchEvent(name, page, allAnswer);
  }

}
