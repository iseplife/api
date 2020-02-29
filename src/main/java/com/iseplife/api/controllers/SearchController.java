package com.iseplife.api.controllers;

import org.springframework.data.domain.Page;
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
/*

  @GetMapping("/student")
  @RolesAllowed({Roles.STUDENT})
  public List<SearchItemView> globalSearch(String name, String promos, Integer page, @RequestParam(defaultValue = "0") Boolean allAnswer) {
    return searchService.globalSearch(name, promos, page, allAnswer);
  }
*/

  @GetMapping("/student/all")
  @RolesAllowed({Roles.STUDENT})
  public Page<SearchItemView> userSearch(String name, String promos, Integer page, @RequestParam(defaultValue = "0") Boolean allAnswer) {
    return searchService.searchUser(name, promos, page, allAnswer);
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
