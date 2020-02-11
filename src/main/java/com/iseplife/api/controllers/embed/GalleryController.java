package com.iseplife.api.controllers.embed;

import com.iseplife.api.constants.Roles;
import com.iseplife.api.entity.post.embed.media.Image;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.services.GalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/gallery")
public class GalleryController {

  @Autowired
  GalleryService galleryService;

  @PostMapping("")
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public Gallery createGallery(@RequestParam("post") Long postID,
                               @RequestParam("name") String name,
                               @RequestParam("images[]") List<MultipartFile> images) {
    return galleryService.createGallery(postID, name, images);
  }

  @GetMapping("/{id}")
  public Gallery getGallery(@PathVariable Long id) {
    return galleryService.getGallery(id);
  }

  @GetMapping("/gallery/{id}/images")
  public List<Image> getGalleryImages(@PathVariable Long id) {
    return galleryService.getGalleryImages(id);
  }

  @PutMapping("/gallery/{id}/images")
  @RolesAllowed({Roles.STUDENT})
  public void addGalleryImages(@RequestParam("images[]") List<MultipartFile> images,
                               @PathVariable Long id) {
    galleryService.addImagesGallery(id, images);
  }

  @PutMapping("/gallery/{id}/images/remove")
  @RolesAllowed({Roles.STUDENT})
  public void deleteGalleryImages(@RequestBody List<Long> images,
                                  @PathVariable Long id) {
    galleryService.deleteImagesGallery(id, images);
  }

}
