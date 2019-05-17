package com.iseplive.api.controllers.media;

import com.iseplive.api.conf.jwt.TokenPayload;
import com.iseplive.api.constants.Roles;
import com.iseplive.api.entity.club.Club;
import com.iseplive.api.entity.media.*;
import com.iseplive.api.exceptions.AuthException;
import com.iseplive.api.services.AuthService;
import com.iseplive.api.services.ClubService;
import com.iseplive.api.services.MediaService;
import com.iseplive.api.utils.MediaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import java.io.File;
import java.util.List;

/**
 * Created by Guillaume on 29/07/2017.
 * back
 */
@RestController
@RequestMapping("/media")
public class MediaController {
  @Autowired
  MediaUtils mediaUtils;

  @Autowired
  MediaService mediaService;

  @Autowired
  AuthService authService;

  @Autowired
  ClubService clubService;

  @GetMapping
  public Page<Media> getAllMedia(@RequestParam(defaultValue = "0") int page,
                                 @AuthenticationPrincipal TokenPayload auth) {
    if (authService.isUserAnonymous()) {
      return mediaService.getAllGalleryGazetteVideoPublic(page);
    }else if(!auth.getRoles().contains(Roles.ADMIN) && !auth.getRoles().contains(Roles.CLUB_MANAGER)){
      return mediaService.getAllGalleryGazetteVideoPublished(page);
    }
    return mediaService.getAllGalleryGazetteVideo(page);
  }

  @PostMapping("/image")
  @RolesAllowed({Roles.ADMIN, Roles.POST_MANAGER, Roles.STUDENT})
  public Image addStandaloneImage(@RequestParam("post") Long postId,
                                  @RequestParam("image") MultipartFile image) {
    return mediaService.addSingleImage(postId, image);
  }

  @PostMapping("/video")
  @RolesAllowed({Roles.ADMIN, Roles.POST_MANAGER, Roles.STUDENT})
  public Video uploadVideo(@RequestParam("name") String name,
                           @RequestParam("post") Long postId,
                           @RequestParam("video") MultipartFile video) {
    return mediaService.uploadVideo(postId, name, video);
  }

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

//  @PostMapping("/videoEmbed")
//  public VideoEmbed addVideoEmbed(@RequestBody VideoEmbedDTO dto) {
//    return mediaService.createVideoEmbed(dto);
//  }

  @PostMapping("/gallery")
  @RolesAllowed({Roles.ADMIN, Roles.POST_MANAGER, Roles.STUDENT})
  public Gallery createGallery(@RequestParam("post") Long postId,
                               @RequestParam("name") String name,
                               @RequestParam("images[]") List<MultipartFile> images) {
    return mediaService.createGallery(postId, name, images);
  }

  @GetMapping("/gallery/{id}")
  public Gallery getGallery(@PathVariable Long id) {
    return mediaService.getGallery(id);
  }

  @GetMapping("/gallery/{id}/images")
  public List<Image> getGalleryImages(@PathVariable Long id) {
    return mediaService.getGalleryImages(id);
  }

  @PutMapping("/gallery/{id}/images")
  @RolesAllowed({Roles.STUDENT})
  public void addGalleryImages(@RequestParam("images[]") List<MultipartFile> images,
                               @PathVariable Long id,
                               @AuthenticationPrincipal TokenPayload payload) {
    Gallery gallery = mediaService.getGallery(id);
    if (!payload.getRoles().contains(Roles.ADMIN) && !payload.getRoles().contains(Roles.POST_MANAGER)) {
      if (!payload.getClubsAdmin().contains(gallery.getPost().getAuthor().getId())) {
        throw new AuthException("you cannot edit this gallery");
      }
    }
    mediaService.addImagesGallery(gallery, images);
  }

  @PutMapping("/gallery/{id}/images/remove")
  @RolesAllowed({Roles.STUDENT})
  public void deleteGalleryImages(@RequestBody List<Long> images,
                                  @PathVariable Long id,
                                  @AuthenticationPrincipal TokenPayload payload) {
    Gallery gallery = mediaService.getGallery(id);
    if (!payload.getRoles().contains(Roles.ADMIN) && !payload.getRoles().contains(Roles.POST_MANAGER)) {
      if (!payload.getClubsAdmin().contains(gallery.getPost().getAuthor().getId())) {
        throw new AuthException("you cannot edit this gallery");
      }
    }
    mediaService.deleteImagesGallery(id, images);
  }


  @PostMapping("/gazette")
  @RolesAllowed({Roles.ADMIN, Roles.POST_MANAGER, Roles.STUDENT})
  public Gazette createGazette(@RequestParam("post") Long postId,
                               @RequestParam("title") String title,
                               @RequestParam("file") MultipartFile file,
                               @AuthenticationPrincipal TokenPayload auth) {
    Club club = clubService.getIsepLive();
    if (!auth.getClubsAdmin().contains(club.getId())) {
      throw new AuthException("you cannot create a gazette");
    }
    return mediaService.createGazette(postId, title, file);
  }

  @PostMapping("/document")
  @RolesAllowed({Roles.ADMIN, Roles.POST_MANAGER, Roles.STUDENT})
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

  @DeleteMapping("/ressource/{type}/{filename:.+}")
  @RolesAllowed({Roles.ADMIN})
  public boolean deleteRessource(@PathVariable String type, @PathVariable String filename) {
    String baseUrl = mediaUtils.getBaseUrl();
    File file = new File(baseUrl + "/" + type + "/" + filename);
    return file.delete();
  }

}
