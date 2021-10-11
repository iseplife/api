package com.iseplife.api.services;

import com.iseplife.api.constants.PostState;
import com.iseplife.api.dao.gallery.GalleryFactory;
import com.iseplife.api.dao.gallery.GalleryRepository;
import com.iseplife.api.dao.media.image.ImageRepository;
import com.iseplife.api.dao.post.PostRepository;
import com.iseplife.api.dto.gallery.GalleryDTO;
import com.iseplife.api.dto.gallery.view.GalleryPreview;
import com.iseplife.api.dto.gallery.view.GalleryView;
import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.post.embed.media.Image;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.exceptions.HttpForbiddenException;
import com.iseplife.api.exceptions.HttpBadRequestException;
import com.iseplife.api.exceptions.HttpNotFoundException;
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
  PostRepository postRepository;

  @Autowired
  PostService postService;

  @Autowired
  StudentService studentService;

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

  private void checkIfHasRightsOnGallery(Gallery gallery){
    if ((gallery.getPseudo() && !SecurityService.hasRightOn(postService.getPostFromEmbed(gallery))) || !SecurityService.hasRightOn(gallery))
      throw new HttpForbiddenException("insufficient_rights");
  }

  public Gallery getGallery(Long id) {
    Optional<Gallery> gallery = galleryRepository.findById(id);
    if (gallery.isEmpty())
      throw new HttpNotFoundException("gallery_not_found");

    return gallery.get();
  }

  public GalleryView getGalleryView(Long id) {
    return GalleryFactory.toView(getGallery(id));
  }

  public List<Image> getGalleryImages(Long id) {
    return getGallery(id)
      .getImages();
  }

  public Page<GalleryPreview> getEventGalleries(Event event, int page) {
    return galleryRepository.findAllByFeedAndPseudoIsFalse(event.getFeed(), PageRequest.of(page, GALLERY_PER_PAGE))
      .map(GalleryFactory::toPreview);
  }

  public Page<GalleryPreview> getClubGalleries(Club club, int page) {
    return galleryRepository.findAllByClubOrderByCreationDesc(club, PageRequest.of(page, GALLERY_PER_PAGE))
      .map(GalleryFactory::toPreview);
  }


  public GalleryView createGallery(GalleryDTO dto) {
    Gallery gallery = new Gallery();
    if (dto.getPseudo() && dto.getImages().size() > PSEUDO_GALLERY_MAX_SIZE)
      throw new HttpBadRequestException("pseudo_gallery_max_size_reached");


    gallery.setCreation(new Date());
    gallery.setPseudo(dto.getPseudo());
    gallery.setFeed(feedService.getFeed(dto.getFeed()));
    if (!dto.getPseudo()) {
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
    if(!dto.getPseudo() && dto.getGeneratePost()){
      Post post = new Post();
      post.setFeed(gallery.getFeed());
      post.setThread(new Thread());
      post.setDescription(gallery.getDescription());
      post.setEmbed(gallery);
      post.setAuthor(studentService.getStudent(SecurityService.getLoggedId()));
      post.setLinkedClub(gallery.getClub());
      post.setCreationDate(new Date());
      post.setState(PostState.READY);

      postRepository.save(post);
    }

    return GalleryFactory.toView(gallery);
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
      .forEach(img -> mediaService.deleteMedia(img));
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

    imageRepository.findAllById(images).forEach(img ->
      mediaService.deleteMedia(img)
    );
  }
}
