package com.iseplife.api.services;

import com.iseplife.api.constants.PublishStateEnum;
import com.iseplife.api.dao.GalleryRepository;
import com.iseplife.api.dao.media.image.ImageRepository;
import com.iseplife.api.dto.TempFile;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.media.Image;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.exceptions.AuthException;
import com.iseplife.api.exceptions.FileException;
import com.iseplife.api.exceptions.IllegalArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class GalleryService {

  private final Logger LOG = LoggerFactory.getLogger(GalleryService.class);

  @Autowired
  GalleryRepository galleryRepository;

  @Autowired
  ImageRepository imageRepository;

  @Autowired
  AuthService authService;

  @Autowired
  PostService postService;

  @Autowired
  MediaService mediaService;

  private static final int GALLERY_PER_PAGE = 5;

  public Gallery getGallery(Long id) {
    Gallery gallery = galleryRepository.findOne(id);
    if (gallery == null) {
      throw new IllegalArgumentException("Could not find this gallery (id:" + id + ")");
    }
    return gallery;

  }

  public List<Image> getGalleryImages(Long id) {
    return getGallery(id)
      .getImages();
  }


  public List<Gallery> getEventGalleries(Event event) {
    return galleryRepository.findAllByFeed(event.getFeed());
  }

  public Page<Gallery> getClubGalleries(Club club, int page) {
    return galleryRepository.findAllByClub(club, new PageRequest(page, GALLERY_PER_PAGE));
  }

  public Gallery createGallery(Long postID, String name, List<MultipartFile> files) {
    Gallery gallery = new Gallery();
    gallery.setName(name);
    gallery.setCreation(new Date());

    galleryRepository.save(gallery);
    postService.addMediaEmbed(postID, gallery);

    List<TempFile> tempFiles = new ArrayList<>();
    try {
      Path galleryTmpDirectory = Files.createTempDirectory("gallery");
      files.forEach(f -> {
        try {
          File tempFile = Files.createTempFile(galleryTmpDirectory, f.getOriginalFilename(), null).toFile();
          TempFile tempFileData = new TempFile(f.getContentType(), tempFile);
          f.transferTo(tempFile);
          tempFiles.add(tempFileData);
        } catch (IOException e) {
          LOG.error("could not create tmp image from gallery: {}", f.getOriginalFilename(), e);
        }
      });
    } catch (IOException e) {
      LOG.error("could not create tmp gallery directory", e);
      throw new FileException("could not create tmp directory");
    }


    CompletableFuture.runAsync(() -> {
      tempFiles.forEach(file -> {
        mediaService.addGalleryImage(file.getFile(), file.getContentType(), gallery);
      });
      postService.setPublishState(postID, PublishStateEnum.PUBLISHED);
    });

    return gallery;
  }

  public void addImagesGallery(Long galleryID, List<MultipartFile> files) {
    Gallery gallery = getGallery(galleryID);
    if (authService.hasRightOn(gallery)) {
      throw new AuthException("You have not sufficient rights on this gallery (id:" + galleryID + ")");
    }

    List<Image> images = new ArrayList<>();
    files.forEach(file ->
      images.add(mediaService.addGalleryImage(file, gallery))
    );
    galleryRepository.save(gallery);
  }


  public void deleteImagesGallery(Long galleryID, List<Long> imagesID) {
    Gallery gallery = getGallery(galleryID);
    if (authService.hasRightOn(gallery)) {
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
      mediaService.deleteImageFile(img);
    });
  }
}
