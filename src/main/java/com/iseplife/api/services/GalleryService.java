package com.iseplife.api.services;

import com.iseplife.api.dao.GalleryRepository;
import com.iseplife.api.dao.media.image.ImageRepository;
import com.iseplife.api.dto.embed.GalleryDTO;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.post.embed.media.Image;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.exceptions.AuthException;
import com.iseplife.api.exceptions.IllegalArgumentException;
import com.iseplife.api.services.fileHandler.FileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GalleryService {

  private final Logger LOG = LoggerFactory.getLogger(GalleryService.class);

  @Autowired
  GalleryRepository galleryRepository;

  @Autowired
  ImageRepository imageRepository;

  @Autowired
  PostService postService;

  @Autowired
  MediaService mediaService;

  @Autowired
  FeedService feedService;

  @Autowired
  ClubService clubService;

  @Qualifier("FileHandlerBean")
  @Autowired
  FileHandler fileHandler;

  private static final int GALLERY_PER_PAGE = 5;
  private static final int PSEUDO_GALLERY_MAX_SIZE = 5;

  public Gallery getGallery(Long id) {
    Optional<Gallery> gallery = galleryRepository.findById(id);
    if (gallery.isEmpty())
      throw new IllegalArgumentException("Could not find this gallery (id:" + id + ")");

    return gallery.get();
  }

  public List<Image> getGalleryImages(Long id) {
    return getGallery(id)
      .getImages();
  }

  public List<Gallery> getEventGalleries(Event event) {
    return galleryRepository.findAllByFeed(event.getFeed());
  }

  public Page<Gallery> getClubGalleries(Club club, int page) {
    return galleryRepository.findAllByClub(club, PageRequest.of(page, GALLERY_PER_PAGE));
  }


  public Gallery createGallery(GalleryDTO dto) {
    Gallery gallery = new Gallery();
    if (dto.getPseudo() && dto.getImages().size() > PSEUDO_GALLERY_MAX_SIZE)
      throw new IllegalArgumentException("pseudo gallery can't have more than " + PSEUDO_GALLERY_MAX_SIZE + " images");

    if (!dto.getPseudo()) {
      Club club = clubService.getClub(dto.getClub());
      if (AuthService.hasRightOn(club))
        throw new AuthException("You have not sufficient rights on this club (id:" + dto.getClub() + ")");

      gallery.setClub(club);
      gallery.setName(dto.getName());
    }

    gallery.setCreation(new Date());
    gallery.setPseudo(dto.getPseudo());
    gallery.setFeed(feedService.getFeed(dto.getFeed()));

    Iterable<Image> images = imageRepository.findAllById(dto.getImages());
    images.forEach(img -> {
      if (img.getGallery() == null && img.getName().startsWith("img/g"))
        img.setGallery(gallery);
    });
    gallery.setImages((List<Image>) images);

    galleryRepository.save(gallery);
    imageRepository.saveAll(images);
    return gallery;
  }

  public void addImagesGallery(Long galleryID, List<Long> images) {
    Gallery gallery = getGallery(galleryID);
    if (!AuthService.hasRightOn(gallery))
      throw new AuthException("You have not sufficient rights on this gallery (id:" + galleryID + ")");

    gallery.getImages().addAll(
      (List<Image>) imageRepository.findAllById(images)
    );
    galleryRepository.save(gallery);
  }


  public void deleteGallery(Gallery gallery) {
    gallery
      .getImages()
      .forEach(img -> mediaService.deleteMedia(img));

    galleryRepository.delete(gallery);
  }


  public void deleteImagesGallery(Long galleryID, List<Long> imagesID) {
    Gallery gallery = getGallery(galleryID);
    if (AuthService.hasRightOn(gallery)) {
      throw new AuthException("You have not sufficient rights on this gallery (id:" + galleryID + ")");
    }

    long imageSize = getGalleryImages(galleryID)
      .stream()
      .filter(img -> imagesID.contains(img.getId()))
      .count();

    if (imagesID.size() != imageSize) {
      throw new IllegalArgumentException("images does not belong to this gallery");
    }

    List<Image> images = imageRepository.findImageByIdIn(imagesID);
    images.forEach(img -> {
      mediaService.deleteMedia(img);
    });
  }
}
