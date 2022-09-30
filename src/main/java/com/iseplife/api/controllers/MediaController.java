package com.iseplife.api.controllers;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dao.media.MediaFactory;
import com.iseplife.api.dto.media.view.MediaView;
import com.iseplife.api.entity.post.embed.media.Matched;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.services.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {
  final private MediaService mediaService;
  final private MediaFactory factory;

  @PostMapping
  @RolesAllowed({Roles.STUDENT})
  public MediaView createMedia(
    @RequestParam(defaultValue = "0") Long club,
    @RequestParam(defaultValue = "0") Boolean gallery,
    @RequestParam(defaultValue = "0") Boolean nsfw,
    @RequestParam(defaultValue = "0") Float ratio,
    @RequestParam(defaultValue = "fff") String color,
    @RequestParam(defaultValue = "document") String type,
    @RequestParam("file") MultipartFile file
  ){
    return factory.toBasicView(mediaService.createMedia(file, club, gallery, nsfw, color, ratio, type));
  }

  @PutMapping("/{id}/nsfw")
  public boolean toggleNSFW(@PathVariable Long id){ return mediaService.toggleNSFW(id); }

  @GetMapping("/image/{id}/tags")
  public List<Matched> getImageTags(@PathVariable Long id) {
    return mediaService.getImageTags(id);
  }

  @PutMapping("/image/{id}/match/{student}/tag")
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER, Roles.STUDENT})
  public void tagStudentInImage(@PathVariable Long id,
                                 @PathVariable Long student,
                                 @AuthenticationPrincipal TokenPayload auth) {
    mediaService.tagStudentInImage(id, student, auth);
  }

  @PutMapping("/image/{id}/match/{student}/untag")
  @RolesAllowed({Roles.ADMIN, Roles.USER_MANAGER, Roles.STUDENT})
  public void untagStudentInImage(@PathVariable Long id,
                                  @PathVariable Long student,
                                  @AuthenticationPrincipal TokenPayload auth) {
    mediaService.untagStudentInImage(id, student, auth);
  }
}
