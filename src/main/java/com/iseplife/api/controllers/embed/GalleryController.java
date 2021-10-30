package com.iseplife.api.controllers.embed;

import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.gallery.GalleryFactory;
import com.iseplife.api.dto.gallery.GalleryDTO;
import com.iseplife.api.dto.gallery.view.GalleryView;
import com.iseplife.api.entity.post.embed.media.Image;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.services.GalleryService;
import com.iseplife.api.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/gallery")
@RequiredArgsConstructor
public class GalleryController {
  final private GalleryService galleryService;
  final private PostService postService;
  final private GalleryFactory factory;

  @PostMapping
  @RolesAllowed({Roles.ADMIN, Roles.STUDENT})
  public GalleryView createGallery(@RequestBody GalleryDTO dto) {
    return factory.toView(galleryService.createGallery(dto));
  }

  @GetMapping("/{id}")
  public GalleryView getGallery(@PathVariable Long id) {
    return factory.toView(galleryService.getGallery(id));
  }

  @DeleteMapping("/{id}")
  public void deleteGallery(@PathVariable Long id) {
    Gallery gallery = galleryService.getGallery(id);
    postService.deletePost(
      postService.getPostFromEmbed(gallery).getId()
    );

    galleryService.deleteGallery(gallery);
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
