package com.iseplife.api.controllers.media;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.entity.post.embed.media.Image;
import com.iseplife.api.entity.Matched;
import com.iseplife.api.entity.post.embed.media.Document;
import com.iseplife.api.entity.post.embed.media.Media;
import com.iseplife.api.entity.post.embed.media.Video;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.services.AuthService;
import com.iseplife.api.services.ClubService;
import com.iseplife.api.services.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/media")
public class MediaController {
  @Autowired
  MediaService mediaService;

  @Autowired
  AuthService authService;

  @Autowired
  ClubService clubService;

  @GetMapping
  public Page<Media> getAllMedia(@RequestParam(defaultValue = "0") int page,
                                 @AuthenticationPrincipal TokenPayload auth) {
    if(!auth.getRoles().contains(Roles.ADMIN)){
      return mediaService.getAllGalleryGazetteVideoPublished(page);
    }
    return mediaService.getAllGalleryGazetteVideo(page);
  }

  @PostMapping
  @RolesAllowed({Roles.STUDENT})
  public Media createMedia(@RequestParam("file") MultipartFile file){
    return mediaService.createMedia(file);
  }

  @DeleteMapping("/{filename}")
  @RolesAllowed({Roles.ADMIN})
  public boolean deleteMedia(@PathVariable String filename) {
    return mediaService.removeMedia(filename);
  }

  @PostMapping("/image")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public void addImages(@RequestParam("post") Long postId, @RequestParam("images") List<MultipartFile> images) {
    mediaService.addImages(postId, images);
  }

  @PostMapping("/video")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public Video uploadVideo(@RequestParam("name") String name,
                           @RequestParam("post") Long postId,
                           @RequestParam("video") MultipartFile video) {
    return mediaService.uploadVideo(postId, name, video);
  }

  @PutMapping("/image/{id}/nsfw")
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
