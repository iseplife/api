package com.iseplife.api.services;

import com.iseplife.api.dao.gallery.GalleryRepository;
import com.iseplife.api.dao.media.image.ImageRepository;
import com.iseplife.api.dto.gallery.GalleryDTO;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.post.embed.media.Image;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.exceptions.http.HttpForbiddenException;
import com.iseplife.api.exceptions.http.HttpBadRequestException;
import com.iseplife.api.exceptions.http.HttpNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GalleryService {
  @Lazy final private PostService postService;
  @Lazy final private MediaService mediaService;
  @Lazy final private FeedService feedService;
  @Lazy final private ClubService clubService;
  final private GalleryRepository galleryRepository;
  final private ImageRepository imageRepository;

  final private static int GALLERY_PER_PAGE = 5;
  final private static int PSEUDO_GALLERY_MAX_SIZE = 5;

  private void checkIfHasRightsOnGallery(Gallery gallery){
    if ((gallery.isPseudo() && !SecurityService.hasRightOn(postService.getPostFromEmbed(gallery))) || !SecurityService.hasRightOn(gallery))
      throw new HttpForbiddenException("insufficient_rights");
  }

  public Gallery getGallery(Long id) {
    Optional<Gallery> gallery = galleryRepository.findById(id);
    if (gallery.isEmpty())
      throw new HttpNotFoundException("gallery_not_found");

    return gallery.get();
  }

  public List<Image> getGalleryImages(Long id) {
    return getGallery(id)
      .getImages();
  }

  public Page<Gallery> getEventGalleries(Event event, int page) {
    return galleryRepository.findAllByFeedAndPseudoIsFalse(event.getFeed(), PageRequest.of(page, GALLERY_PER_PAGE));
  }

  public Page<Gallery> getClubGalleries(Club club, int page) {
    return galleryRepository.findAllByClubOrderByCreationDesc(club, PageRequest.of(page, GALLERY_PER_PAGE));
  }


  public Gallery createGallery(GalleryDTO dto) {
    Gallery gallery = new Gallery();
    if (dto.isPseudo() && dto.getImages().size() > PSEUDO_GALLERY_MAX_SIZE)
      throw new HttpBadRequestException("pseudo_gallery_max_size_reached");


    gallery.setCreation(new Date());
    gallery.setPseudo(dto.isPseudo());
    gallery.setFeed(feedService.getFeed(dto.getFeed()));
    if (!dto.isPseudo()) {
      Club club = clubService.getClub(dto.getClub());
      if (!SecurityService.hasRightOn(club))
        throw new HttpForbiddenException("insufficient_rights");

      gallery.setClub(club);
      gallery.setName(dto.getName());
      gallery.setDescription(dto.getDescription());
    }

    galleryRepository.save(gallery);

    Iterable<Image> images = imageRepository.findAllById(dto.getImages());
    images.forEach(img -> {
      // We make sure to not add a same media to multiple gallery
      if (img.getGallery() == null) img.setGallery(gallery);
    });

    imageRepository.saveAll(images);
    if(!dto.isPseudo() && dto.getGeneratePost())
      postService.createPost(gallery);

    return gallery;
  }

  public void addImagesGallery(Long galleryID, List<Long> images) {
    Gallery gallery = getGallery(galleryID);
    checkIfHasRightsOnGallery(gallery);

    Iterable<Image> medias = imageRepository.findAllById(images);
    medias.forEach(img -> {
        if(img.getGallery() != null)
          throw new HttpBadRequestException("image_already_attached");

        img.setGallery(gallery);
    });

    imageRepository.saveAll(medias);
  }


  public void deleteGallery(Gallery gallery) {
    checkIfHasRightsOnGallery(gallery);

    gallery
      .getImages()
      .forEach(mediaService::deleteMedia);
    gallery.setImages(null);

    galleryRepository.delete(gallery);
  }


  public void deleteImagesGallery(Long id, List<Long> images) {
    Gallery gallery = getGallery(id);
    checkIfHasRightsOnGallery(gallery);

    long imageSize = gallery.getImages()
      .stream()
      .filter(img -> images.contains(img.getId()))
      .count();
    if (images.size() != imageSize)
      throw new HttpBadRequestException("images_not_attached_to_gallery");

    imageRepository
      .findAllById(images)
      .forEach(mediaService::deleteMedia);
  }

}
