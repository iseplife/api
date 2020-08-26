package com.iseplife.api.controllers;


import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dao.group.GroupFactory;
import com.iseplife.api.dto.group.groupDTO;
import com.iseplife.api.dto.group.view.GroupPreview;
import com.iseplife.api.dto.group.view.GroupView;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.services.AuthService;
import com.iseplife.api.services.GroupService;
import com.iseplife.api.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import java.util.List;


@RestController
@RequestMapping("/group")
public class GroupController {

  @Autowired
  private GroupService groupService;

  @Autowired
  JsonUtils jsonUtils;

  @Autowired
  AuthService authService;

  @GetMapping
  @RolesAllowed({Roles.ADMIN})
  public Page<GroupView> getAll(@RequestParam(defaultValue = "0") int page) {
    return groupService.getAll(page);
  }

  @GetMapping("/me")
  @RolesAllowed({Roles.STUDENT})
  public List<GroupPreview> getUserGroups(@AuthenticationPrincipal TokenPayload token) {
    return groupService.getUserGroups(token);
  }


  @PostMapping
  @RolesAllowed({Roles.ADMIN})
  public GroupView createGroup(
    @RequestParam(name="form") String form,
    @RequestParam(name="file", required = false) MultipartFile file
  ) {
    groupDTO dto = jsonUtils.deserialize(form, groupDTO.class);
    return groupService.createGroup(dto, file);
  }

  @GetMapping("/{id}")
  @RolesAllowed({Roles.ADMIN})
  public GroupView getGroup(@PathVariable Long id) {
    return GroupFactory.toView(groupService.getGroup(id));
  }

  @PutMapping("/{id}")
  @RolesAllowed({Roles.ADMIN})
  public GroupView updateGroup(
    @PathVariable Long id,
    @RequestParam(name="form") String form,
    @RequestParam(name="file", required = false) MultipartFile file
  ) {
    groupDTO dto = jsonUtils.deserialize(form, groupDTO.class);
    return groupService.updateGroup(id, dto, file);
  }

  @PutMapping("/{id}/archive")
  @RolesAllowed({Roles.ADMIN})
  public Boolean toggleArchiveStatus(@PathVariable Long id) {
    return groupService.toggleArchive(id);
  }

  @DeleteMapping("/{id}")
  @RolesAllowed({Roles.ADMIN})
  public void deleteGroup(@PathVariable Long id) {
    groupService.deleteGroup(id);
  }
}
