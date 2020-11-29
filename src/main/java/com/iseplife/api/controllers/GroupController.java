package com.iseplife.api.controllers;


import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dto.group.GroupDTO;
import com.iseplife.api.dto.group.view.GroupPreview;
import com.iseplife.api.dto.group.view.GroupView;
import com.iseplife.api.constants.Roles;
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

  @GetMapping
  @RolesAllowed({Roles.ADMIN})
  public Page<GroupPreview> getAll(@RequestParam(defaultValue = "0") int page) {
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
    GroupDTO dto = jsonUtils.deserialize(form, GroupDTO.class);
    return groupService.createGroup(dto, file);
  }

  @GetMapping("/{id}")
  @RolesAllowed({Roles.STUDENT})
  public GroupView getGroup(@PathVariable Long id) {
    return groupService.getGroupView(id);
  }

  @PutMapping("/{id}")
  @RolesAllowed({Roles.ADMIN})
  public GroupView updateGroup(@PathVariable Long id, @RequestBody GroupDTO dto) {
    return groupService.updateGroup(id, dto);
  }

  @PostMapping("/{id}/cover")
  @RolesAllowed({Roles.STUDENT})
  public String updateCover(@PathVariable Long id, @RequestParam(value = "file") MultipartFile file) {
    return groupService.updateCover(id, file);
  }

  @DeleteMapping("/{id}/member/{member}")
  @RolesAllowed({Roles.STUDENT})
  public Boolean removeMember(@PathVariable Long id, @PathVariable Long member) {
    return groupService.removeMember(id, member);
  }

  @PostMapping("/{id}/member/{member}/promote")
  @RolesAllowed({Roles.STUDENT})
  public Boolean promoteMember(@PathVariable Long id, @PathVariable Long member) {
    return groupService.promoteMember(id, member);
  }

  @PostMapping("/{id}/member/{member}/demote")
  @RolesAllowed({Roles.STUDENT})
  public Boolean demoteMember(@PathVariable Long id, @PathVariable Long member) {
    return groupService.demoteMember(id, member);
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
