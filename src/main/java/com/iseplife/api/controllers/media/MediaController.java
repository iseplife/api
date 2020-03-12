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
import java.io.File;
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

  @PostMapping("/image")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public Image addStandaloneImage(@RequestParam("post") Long postId,
                                  @RequestParam("image") MultipartFile image) {
    return mediaService.addSingleImage(postId, image);
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




  @PostMapping("/document")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public Document createDocument(@RequestParam("post") Long postId,
                                 @RequestParam("name") String name,
                                 @RequestParam("document") MultipartFile document) {
    return mediaService.createDocument(postId, name, document);
  }

//  @GetMapping(value = "/ressource/video/{filename:.+}", produces = "video/png", headers = "Accept-range: byte")
//  public FileSystemResource streamVideo(@PathVariable String filename, HttpRange range) {
//
//    String baseUrl = mediaUtils.getBaseUrl();
//    File file = new File(baseUrl + "/video/" + filename);
//    return new FileSystemResource(file);
//  }

//  @GetMapping("/ressource/{type}/{filename:.+}")
//  public FileSystemResource downloadRessource(@PathVariable String type, @PathVariable String filename) {
//    String baseUrl = mediaUtils.getBaseUrl();
//    File file = new File(baseUrl + "/" + type + "/" + filename);
//    if (!file.exists()) {
//      System.out.println(file.getPath());
//      throw new NotFoundException("Cannot find this file");
//    }
//    ResourceHttpRequestHandler r = new ResourceHttpRequestHandler();
//    return new FileSystemResource(file);
//  }

  @DeleteMapping("/ressource/{filename:.+}")
  @RolesAllowed({Roles.ADMIN})
  public boolean deleteRessource(@PathVariable String filename) {
    return mediaService.removeMedia(filename);
  }

}
