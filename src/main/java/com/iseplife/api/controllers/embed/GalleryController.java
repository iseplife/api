package com.iseplife.api.controllers.embed;

import com.iseplife.api.constants.Roles;
import com.iseplife.api.dto.gallery.GalleryDTO;
import com.iseplife.api.dto.gallery.view.GalleryView;
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
  public GalleryView createGallery(@RequestBody GalleryDTO dto) {
    return galleryService.createGallery(dto);
  }

  @GetMapping("/{id}")
  public GalleryView getGallery(@PathVariable Long id) {
    return galleryService.getGalleryView(id);
  }

  @DeleteMapping("/{id}")
  public Boolean deleteGallery(@PathVariable Long id) {
    return galleryService.deleteGallery(id);
  }

  @GetMapping("/{id}/images")
  public List<Image> getGalleryImages(@PathVariable Long id) {
    return galleryService.getGalleryImages(id);
  }

  @PutMapping("/{id}/images")
  @RolesAllowed({Roles.STUDENT})
  public void addGalleryImages(@PathVariable Long id, @RequestParam("id") List<Long> images) {
    galleryService.addImagesGallery(id, images);
  }

  @DeleteMapping("/{id}/images")
  @RolesAllowed({Roles.STUDENT})
  public void deleteGalleryImages(@PathVariable Long id, @RequestParam("id") List<Long> images) {
    galleryService.deleteImagesGallery(id, images );
  }

}
