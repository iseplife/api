package com.iseplife.api.services;

import com.iseplife.api.conf.StorageConfig;
import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.MediaType;
import com.iseplife.api.dao.club.ClubRepository;
import com.iseplife.api.dao.gallery.GalleryRepository;
import com.iseplife.api.dao.student.StudentRepository;
import com.iseplife.api.dto.view.MatchedView;
import com.iseplife.api.entity.Author;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.post.embed.media.Image;
import com.iseplife.api.entity.Matched;
import com.iseplife.api.entity.post.embed.media.Document;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.entity.post.embed.media.Media;
import com.iseplife.api.entity.post.embed.media.Video;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.media.image.ImageRepository;
import com.iseplife.api.dao.media.image.MatchedRepository;
import com.iseplife.api.dao.media.MediaRepository;
import com.iseplife.api.dao.post.PostRepository;
import com.iseplife.api.exceptions.AuthException;
import com.iseplife.api.exceptions.IllegalArgumentException;
import com.iseplife.api.exceptions.MediaMaxUploadException;
import com.iseplife.api.services.fileHandler.FileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.Cacheable;

import java.security.InvalidParameterException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;


@Service
public class MediaService {

  private final Logger LOG = LoggerFactory.getLogger(MediaService.class);

  @Value("${media_limit.club}")
  private Integer DAILY_CLUB_MEDIA;

  @Value("${media_limit.user}")
  private Integer DAILY_USER_MEDIA;

  @Autowired
  MediaRepository mediaRepository;

  @Autowired
  MatchedRepository matchedRepository;

  @Autowired
  ImageRepository imageRepository;

  @Autowired
  PostRepository postRepository;

  @Autowired
  GalleryRepository galleryRepository;

  @Autowired
  StudentRepository studentRepository;

  @Autowired
  ClubRepository clubRepository;

  @Autowired
  PostService postService;

  @Autowired
  StudentService studentService;

  @Autowired
  ClubService clubService;

  @Qualifier("FileHandlerBean")
  @Autowired
  FileHandler fileHandler;

  private static final int ALL_MEDIA_PAGE_SIZE = 20;

  private static final int PHOTOS_PER_PAGE = 30;

  public Media getMedia(Long id) {
    Optional<Media> media = mediaRepository.findById(id);
    if (media.isEmpty())
      throw new InvalidParameterException("could not get the image with id: " + id);

    return media.get();
  }

  @Cacheable("media-list-published")
  public Page<Media> getAllGalleryGazetteVideoPublished(int page) {
    Page<Media> list = mediaRepository.findAllByMediaTypeInOrderByCreationDesc(
      Arrays.asList(MediaType.IMAGE, MediaType.VIDEO),
      PageRequest.of(page, ALL_MEDIA_PAGE_SIZE)
    );

    return list;
  }

  @Cacheable("media-list-all")
  public Page<Media> getAllGalleryGazetteVideo(int page) {
    return mediaRepository.findAllByMediaTypeInOrderByCreationDesc(
      Arrays.asList(MediaType.IMAGE, MediaType.VIDEO),
      PageRequest.of(page, ALL_MEDIA_PAGE_SIZE)
    );
  }

  private Image getImage(Long id) {
    Optional<Image> img = imageRepository.findById(id);
    if (img.isEmpty())
      throw new RuntimeException("could not get the image with id: " + id);

    return img.get();
  }


  private Boolean isAllowedToCreateMedia(Author author) {
    boolean isStudent = author instanceof Student;
    if (!isStudent && !SecurityService.hasRightOn((Club) author))
      throw new AuthException("You have not sufficient rights on this club (id:" + author.getId() + ")");

    if (author.getMediaCooldown() == null || Duration.between(author.getMediaCooldown().toInstant(), Instant.now()).toHours() > 24) {
      author.setMediaCooldown(new Date());
      if (isStudent) {
        author.setMediaCounter(DAILY_USER_MEDIA);
        studentRepository.save((Student) author);
      } else {
        author.setMediaCounter(DAILY_CLUB_MEDIA);
        clubRepository.save((Club) author);
      }

      return true;
    } else if (author.getMediaCounter() > 0) {
      author.setMediaCounter(author.getMediaCounter() - 1);
      if (isStudent)
        studentRepository.save((Student) author);
      else
        clubRepository.save((Club) author);

      return true;
    }

    return false;
  }

  public Media createMedia(MultipartFile file, Long club, Boolean gallery, Boolean nsfw) {
    Author author = club > 0 ?
      clubService.getClub(club) :
      studentService.getStudent(SecurityService.getLoggedId());

    if (gallery && club <= 0)
      throw new IllegalArgumentException("Club need to be specified when creating a gallery image");

    if (isAllowedToCreateMedia(author)) {
      String name, mime = file.getContentType().split("/")[0];
      Media media;
      switch (mime) {
        case "video":
          media = new Video();
          name = fileHandler.upload(file, StorageConfig.MEDIAS_CONF.get("video").path, false, Collections.EMPTY_MAP);
          break;
        case "image":
          media = new Image();
          if (gallery) {
            name = fileHandler.upload(file, StorageConfig.MEDIAS_CONF.get("gallery").path, false,
              Map.of(
                "process", "resize",
                "sizes", StorageConfig.MEDIAS_CONF.get("gallery").sizes
              )
            );
          } else {
            name = fileHandler.upload(file, StorageConfig.MEDIAS_CONF.get("post").path, false,
              Map.of(
                "process", "compress",
                "sizes", StorageConfig.MEDIAS_CONF.get("post").sizes
              )
            );
          }
          break;
        default:
          media = new Document();
          name = fileHandler.upload(file, StorageConfig.MEDIAS_CONF.get("document").path, false, Collections.EMPTY_MAP);
          break;
      }

      media.setName(name);
      media.setNSFW(nsfw);
      media.setCreation(new Date());
      return mediaRepository.save(media);
    }

    throw new MediaMaxUploadException("You've reached your maximum daily upload");
  }

  public boolean toggleNSFW(Long id) {
    Media media = getImage(id);
    media.setNSFW(!media.isNSFW());

    mediaRepository.save(media);
    return media.isNSFW();
  }

  public boolean removeMedia(String filename) {
    return fileHandler.delete(filename);
  }


  void deleteMedia(Media media) {
    String name = media.getName();
    fileHandler.delete(name);
    mediaRepository.delete(media);
  }

  /**
   * Get all people linked to an image
   *
   * @param id
   * @return
   */
  public List<Matched> getImageTags(Long id) {
    Image image = getImage(id);

    return image.getMatched();
  }

  /**
   * Get all photos tagged by a student
   *
   * @param studentId
   * @param page
   * @return
   */
  public Page<MatchedView> getPhotosTaggedByStudent(Long studentId, int page) {
    return matchedRepository.findAllByMatchId(studentId, PageRequest.of(page, PHOTOS_PER_PAGE)).map(m -> {
      MatchedView matchedView = new MatchedView();
      matchedView.setId(m.getId());
      matchedView.setImage(m.getImage());
      matchedView.setOwner(m.getOwner());
      Gallery gallery = m.getImage().getGallery();
      if (gallery != null) {
        matchedView.setGalleryId(gallery.getId());
      }
      return matchedView;
    });
  }

  /**
   * Tag a student in an image
   *
   * @param imageId
   * @param studentId
   * @param auth
   */
  public void tagStudentInImage(Long imageId, Long studentId, TokenPayload auth) {
    Image image = getImage(imageId);
    List<Matched> matchedList = matchedRepository.findAllByImage(image);
    int res = (int) matchedList.stream()
      .filter(m -> m.getMatch().getId().equals(studentId))
      .count();

    if (res > 0) {
      throw new IllegalArgumentException("this user is already tagged");
    }
    Student match = studentService.getStudent(studentId);
    Student owner = studentService.getStudent(auth.getId());
    Matched matched = new Matched();
    matched.setMatch(match);
    matched.setOwner(owner);
    matched.setImage(image);
    matchedRepository.save(matched);
  }

  /**
   * Untag a student in an image
   *
   * @param imageId
   * @param studentId
   * @param auth
   */
  public void untagStudentInImage(Long imageId, Long studentId, TokenPayload auth) {
    Image image = getImage(imageId);
    List<Matched> matchedList = matchedRepository.findAllByImage(image);
    Student match = studentService.getStudent(studentId);
    Student owner = studentService.getStudent(auth.getId());
    matchedList.forEach(m -> {
      if (m.getMatch().equals(match)) {
        if (auth.getRoles().contains(Roles.ADMIN) || auth.getRoles().contains(Roles.USER_MANAGER)) {
          matchedRepository.delete(m);
        }

        if (m.getOwner().equals(owner)) {
          matchedRepository.delete(m);
        }
      }
    });
  }

}
