package com.iseplife.api.controllers;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.MediaStatus;
import com.iseplife.api.dao.media.MediaFactory;
import com.iseplife.api.dto.media.view.MediaView;
import com.iseplife.api.entity.post.embed.media.Matched;
import com.iseplife.api.exceptions.http.HttpBadRequestException;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.services.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {
  final private MediaService mediaService;
  final private MediaFactory factory;

  @Value("${aws-lambda.secret-token}")
  private String SECRET_TOKEN;

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
    try {
      return factory.toBasicView(mediaService.createMedia(file, club, gallery, nsfw, color, ratio, type));
    } catch (IOException e) {
      e.printStackTrace();
      throw new HttpBadRequestException("media_upload_failed", e);
    }
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

  @PostMapping("/lambda/{name}/set-state/{status}")
  public void updateImageProcessingStatus(@PathVariable String name, @PathVariable MediaStatus status, @RequestParam() String secret_token){
    if(!SECRET_TOKEN.equals(secret_token)) {
      System.out.println("Refused '"+secret_token+"' secret token. Should be '"+SECRET_TOKEN+"'.");
      throw new HttpBadRequestException("invalid_secret_token");
    }

    if(status == null)
      throw new HttpBadRequestException("invalid_media_status");

    System.out.println("Updating media processing status for " + name + " to " + status);

    mediaService.updateMediaProcessingStatus(name, status);
  }
}
