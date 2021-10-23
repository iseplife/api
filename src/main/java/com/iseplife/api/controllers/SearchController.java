package com.iseplife.api.controllers;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dto.view.SearchItemView;
import com.iseplife.api.services.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
  final private SearchService searchService;

  @GetMapping
  @RolesAllowed({Roles.STUDENT})
  public Page<SearchItemView> globalSearch(
    @AuthenticationPrincipal TokenPayload token,
    @RequestParam String name,
    @RequestParam(required = false, defaultValue = "0") Integer page,
    @RequestParam(required = false, defaultValue = "0") Boolean allAnswer
  ) {
    return searchService.globalSearch(name, page, allAnswer, token);
  }

  @GetMapping("/student")
  @RolesAllowed({Roles.STUDENT})
  public Page<SearchItemView> userSearch(
    @RequestParam String name,
    @RequestParam(required = false, defaultValue = "") String promos,
    @RequestParam(required = false, defaultValue = "0") Integer page,
    @RequestParam(required = false, defaultValue = "0") Boolean atoz
  ) {
    return searchService.searchUser(name, promos, atoz, page);
  }

  @GetMapping("/student/all")
  @RolesAllowed({Roles.STUDENT})
  public List<SearchItemView> userSearchAll(@RequestParam String name) {
    return searchService.searchUserAll(name);
  }

  @GetMapping("/club")
  @RolesAllowed({Roles.STUDENT})
  public Page<SearchItemView> clubSearch(
    @RequestParam String name,
    @RequestParam(required = false, defaultValue = "0") Integer page,
    @RequestParam(required = false, defaultValue = "0") Boolean allAnswer
  ) {
    return searchService.searchClub(name, page, allAnswer);
  }

  @GetMapping("/event")
  @RolesAllowed({Roles.STUDENT})
  public Page<SearchItemView> eventSearch(
    @AuthenticationPrincipal TokenPayload token,
    @RequestParam String name,
    @RequestParam(required = false, defaultValue = "0") Integer page,
    @RequestParam(required = false, defaultValue = "0") Boolean allAnswer
  ) {
    return searchService.searchEvent(name, page, allAnswer, token);
  }

}
