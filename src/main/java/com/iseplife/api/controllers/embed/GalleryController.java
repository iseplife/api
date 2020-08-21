package com.iseplife.api.controllers.embed;

import com.iseplife.api.constants.Roles;
import com.iseplife.api.dto.gallery.GalleryDTO;
import com.iseplife.api.entity.post.embed.media.Image;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.services.GalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/gallery")
public class GalleryController {

  @Autowired
  GalleryService galleryService;

  @PostMapping
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public Gallery createGallery(@RequestBody GalleryDTO dto) {
    return galleryService.createGallery(dto);
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
  public void addGalleryImages(@PathVariable Long id, @RequestParam("images") List<Long> images) {
    galleryService.addImagesGallery(id, images);
  }

  @PutMapping("/gallery/{id}/images/remove")
  @RolesAllowed({Roles.STUDENT})
  public void deleteGalleryImages(@RequestBody List<Long> images, @PathVariable Long id) {
    galleryService.deleteImagesGallery(id, images);
  }

}
