package com.iseplive.api.controllers;

import com.iseplive.api.conf.jwt.TokenPayload;
import com.iseplive.api.constants.ClubRoleEnum;
import com.iseplive.api.constants.Roles;
import com.iseplive.api.dto.ClubDTO;
import com.iseplive.api.dto.view.PostView;
import com.iseplive.api.entity.club.Club;
import com.iseplive.api.entity.club.ClubMember;
import com.iseplive.api.entity.club.ClubRole;
import com.iseplive.api.entity.user.Student;
import com.iseplive.api.exceptions.AuthException;
import com.iseplive.api.services.AuthService;
import com.iseplive.api.services.ClubService;
import com.iseplive.api.services.PostService;
import com.iseplive.api.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Set;

/**
 * Created by Guillaume on 30/07/2017.
 * back
 */
@RestController
@RequestMapping("/club")
public class ClubController {

  @Autowired
  ClubService clubService;

  @Autowired
  PostService postService;

  @Autowired
  AuthService authService;

  @Autowired
  JsonUtils jsonUtils;

  @GetMapping
  public List<Club> listClubs() {
    return clubService.getAll();
  }

  @GetMapping("/search")
  public List<Club> searchClubs(@RequestParam String name) {
    return clubService.searchClubs(name);
  }

  @PostMapping
  @RolesAllowed({Roles.ADMIN, Roles.CLUB_MANAGER})
  public Club createClub(@RequestParam("logo") MultipartFile logo,
                         @RequestParam("club") String club) {
    ClubDTO clubDTO = jsonUtils.deserialize(club, ClubDTO.class);
    return clubService.createClub(clubDTO, logo);
  }

  @PutMapping("/member/{member}/role/{role}")
  @RolesAllowed({Roles.STUDENT})
  public ClubMember updateMemberRole(@PathVariable Long member,
                                     @PathVariable String role,
                                     @AuthenticationPrincipal TokenPayload auth) {
    //TODO: Maybe try/catch this cast
    ClubRoleEnum roleChecked = ClubRoleEnum.valueOf(role);
    return clubService.updateMemberRole(member, roleChecked , auth);
  }

  @DeleteMapping("/member/{member}")
  @RolesAllowed({Roles.STUDENT})
  public void deleteMember(@PathVariable Long member,
                           @AuthenticationPrincipal TokenPayload payload) {
    clubService.removeMember(member, payload);
  }

  @PutMapping("/{id}")
  @RolesAllowed({Roles.STUDENT})
  public Club updateClub(@PathVariable Long id,
                         @RequestParam(value = "logo", required = false) MultipartFile logo,
                         @RequestParam("club") String club,
                         @AuthenticationPrincipal TokenPayload payload) {
    ClubDTO clubDTO = jsonUtils.deserialize(club, ClubDTO.class);
    if (hasAdminAccess(payload, id)) {
      throw new AuthException("no rights to modify this club");
    }
    return clubService.updateClub(id, clubDTO, logo);
  }

  @GetMapping("/{id}")
  public Club getClub(@PathVariable Long id) {
    return clubService.getClub(id);
  }

  @DeleteMapping("/{id}")
  @RolesAllowed({Roles.ADMIN, Roles.CLUB_MANAGER})
  public void deleteClub(@PathVariable Long id) {
    clubService.deleteClub(id);
  }

  private boolean hasAdminAccess(TokenPayload token, Long club) {
    if (!token.getRoles().contains(Roles.ADMIN) && token.getRoles().contains(Roles.CLUB_MANAGER)) {
      return !token.getClubsAdmin().contains(club);
    }
    return false;
  }

  @GetMapping("/{id}/role")
  @RolesAllowed({Roles.STUDENT})
  public List<ClubRole> getClubRoles(@PathVariable Long id) {
    return clubService.getClubRoles(id);
  }

  @PostMapping("/{id}/role/{name}")
  @RolesAllowed({Roles.STUDENT})
  public ClubRole createRole(@PathVariable String name,
                             @PathVariable Long id,
                             @AuthenticationPrincipal TokenPayload auth) {
    if (hasAdminAccess(auth, id)) {
      throw new AuthException("no rights to modify this club");
    }
    return clubService.createRole(name, id);
  }

  @DeleteMapping("/{id}/role/{roleid}")
  @RolesAllowed({Roles.STUDENT})
  public void deleteClubRole(@PathVariable Long id,
                             @PathVariable Long roleid,
                             @AuthenticationPrincipal TokenPayload auth) {
    if (hasAdminAccess(auth, id)) {
      throw new AuthException("no rights to modify this club");
    }
    clubService.deleteClubRole(roleid);
  }

  @PutMapping("/{id}/member/{student}")
  @RolesAllowed({Roles.STUDENT})
  public ClubMember addMember(@PathVariable Long id,
                              @PathVariable Long student,
                              @AuthenticationPrincipal TokenPayload auth) {
    if (hasAdminAccess(auth, id)) {
      throw new AuthException("no rights to modify this club");
    }
    return clubService.addMember(id, student);
  }

  @GetMapping("/{id}/admins")
  public Set<Student> getAdmins(@PathVariable Long id) {
    return clubService.getAdmins(id);
  }

  @PutMapping("/{id}/admin/{stud}")
  @RolesAllowed({Roles.STUDENT})
  public void addAdmin(@PathVariable Long id,
                       @PathVariable Long stud,
                       @AuthenticationPrincipal TokenPayload auth) {
    if (hasAdminAccess(auth, id)) {
      throw new AuthException("no rights to modify this club");
    }
    clubService.addAdmin(id, stud);
  }

  @DeleteMapping("/{id}/admin/{stud}")
  @RolesAllowed({Roles.STUDENT})
  public void deleteAdmin(@PathVariable Long id,
                          @PathVariable Long stud,
                          @AuthenticationPrincipal TokenPayload auth) {
    if (hasAdminAccess(auth, id)) {
      throw new AuthException("no rights to modify this club");
    }
    clubService.removeAdmin(id, stud);
  }

  @GetMapping("/{id}/member")
  public List<ClubMember> getMembers(@PathVariable Long id) {
    return clubService.getMembers(id);
  }

  @GetMapping("/{id}/post")
  public Page<PostView> getPosts(@PathVariable Long id, @RequestParam(defaultValue = "0") int page) {
    return postService.getPostsAuthor(id, authService.isUserAnonymous(), page);
  }
}
