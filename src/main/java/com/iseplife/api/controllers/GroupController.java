package com.iseplife.api.controllers;

import com.iseplife.api.dao.media.MediaFactory;
import com.iseplife.api.dao.group.GroupFactory;
import com.iseplife.api.dto.group.GroupCreationDTO;
import com.iseplife.api.dto.group.GroupMemberDTO;
import com.iseplife.api.dto.group.GroupUpdateDTO;
import com.iseplife.api.dto.group.view.GroupAdminView;
import com.iseplife.api.dto.group.view.GroupMemberView;
import com.iseplife.api.dto.group.view.GroupPreview;
import com.iseplife.api.dto.group.view.GroupView;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dto.media.view.MediaNameView;
import com.iseplife.api.entity.group.Group;
import com.iseplife.api.services.GroupService;
import com.iseplife.api.services.SecurityService;
import com.iseplife.api.services.SubscriptionService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController {
  final private GroupService groupService;
  final private GroupFactory factory;
  final private SubscriptionService subscriptionService;

  @GetMapping
  @RolesAllowed({Roles.ADMIN})
  public Page<GroupPreview> getAll(@RequestParam(defaultValue = "0") int page) {
    return groupService.getAll(page).map(factory::toPreview);
  }

  @GetMapping("/me")
  @RolesAllowed({Roles.STUDENT})
  public List<GroupPreview> getUserGroups() {
    return groupService.getUserGroups(SecurityService.getLoggedId())
      .stream()
      .map(factory::toPreview)
      .collect(Collectors.toList());
  }

  @PostMapping
  @RolesAllowed({Roles.ADMIN})
  public GroupAdminView createGroup(@RequestBody GroupCreationDTO dto) {
    Group group = groupService.createGroup(dto);
    return factory.toAdminView(group, group.getMembers());
  }

  @GetMapping("/{id}")
  @RolesAllowed({Roles.STUDENT})
  public GroupView getGroup(@PathVariable Long id) {
    Group group = groupService.getGroup(id);
    return factory.toView(group, subscriptionService.getSubscriptionProjection(group));
  }

  @GetMapping("/{id}/admin")
  @RolesAllowed({Roles.ADMIN})
  public GroupAdminView getGroupAdmin(@PathVariable Long id) {
    return factory.toAdminView(groupService.getGroup(id), groupService.getGroupAdminMembers(id));
  }

  @PutMapping("/{id}")
  @RolesAllowed({Roles.ADMIN})
  public GroupAdminView updateGroup(@PathVariable Long id, @RequestBody GroupUpdateDTO dto) {
    return  factory.toAdminView(groupService.updateGroup(id, dto), groupService.getGroupAdminMembers(id));
  }

  @PutMapping("/{id}/cover")
  @RolesAllowed({Roles.STUDENT})
  public MediaNameView updateCover(@PathVariable Long id, @RequestParam(value = "file", required = false) MultipartFile file) {
    return MediaFactory.toNameView(groupService.updateCover(id, file));
  }

  @GetMapping("/{id}/member")
  @RolesAllowed({Roles.STUDENT})
  public List<GroupMemberView> getClubMembers(@PathVariable Long id) {
    return groupService.getGroupMembers(id).stream().map(factory::toView).collect(Collectors.toList());
  }

  @PostMapping("/{id}/member")
  @RolesAllowed({Roles.STUDENT})
  public GroupMemberView addMember(@PathVariable Long id, @RequestBody GroupMemberDTO dto) {
    return factory.toView(groupService.addMember(id, dto));
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
