package com.iseplife.api.services;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.iseplife.api.conf.StorageConfig;
import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.constants.ThreadType;
import com.iseplife.api.dao.club.ClubRepository;
import com.iseplife.api.dao.media.MediaRepository;
import com.iseplife.api.dao.media.image.ImageRepository;
import com.iseplife.api.dao.media.image.MatchedRepository;
import com.iseplife.api.dao.student.StudentRepository;
import com.iseplife.api.dto.view.MatchedView;
import com.iseplife.api.entity.Author;
import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.entity.post.embed.media.Document;
import com.iseplife.api.entity.post.embed.media.Image;
import com.iseplife.api.entity.post.embed.media.Matched;
import com.iseplife.api.entity.post.embed.media.Media;
import com.iseplife.api.entity.post.embed.media.Video;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.exceptions.MediaMaxUploadException;
import com.iseplife.api.exceptions.http.HttpBadRequestException;
import com.iseplife.api.exceptions.http.HttpForbiddenException;
import com.iseplife.api.exceptions.http.HttpNotFoundException;
import com.iseplife.api.services.fileHandler.FileHandler;
import com.iseplife.api.utils.RandomString;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class MediaService {
  @Lazy
  final private StudentService studentService;
  @Lazy
  final private ClubService clubService;
  final private MediaRepository mediaRepository;
  final private MatchedRepository matchedRepository;
  final private ImageRepository imageRepository;
  final private StudentRepository studentRepository;
  final private ClubRepository clubRepository;

  @Qualifier("FileHandlerBean")
  final private FileHandler fileHandler;

  @Value("${media_limit.club.amount}")
  private Integer DAILY_CLUB_MEDIA;
  @Value("${media_limit.club.max_size}")
  private Long CLUB_MEDIA_MAX_SIZE;

  @Value("${media_limit.user.amount}")
  private Integer DAILY_USER_MEDIA;
  @Value("${media_limit.user.max_size}")
  private Long USER_MEDIA_MAX_SIZE;

  final private static int PHOTOS_PER_PAGE = 30;

  public Media getMedia(Long id) {
    Optional<Media> media = mediaRepository.findById(id);
    if (media.isEmpty())
      throw new HttpNotFoundException("media_not_found");

    return media.get();
  }

  private Image getImage(Long id) {
    Optional<Image> img = imageRepository.findById(id);
    if (img.isEmpty())
      throw new HttpNotFoundException("image_not_found");

    return img.get();
  }

  private Boolean isAllowedToCreateMedia(Author author, Long fileSize) {
    boolean isStudent = author instanceof Student;
    if (!isStudent && !SecurityService.hasRightOn((Club) author))
      throw new HttpForbiddenException("insufficient_rights");

    if(fileSize > (isStudent ? USER_MEDIA_MAX_SIZE: CLUB_MEDIA_MAX_SIZE) )
      throw new HttpBadRequestException("file_to_big");


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

  public Media createMedia(MultipartFile file, Long club, Boolean gallery, Boolean nsfw, String averageColor, Float ratio) {
    Author author = club > 0 ?
      clubService.getClub(club) :
      studentService.getStudent(SecurityService.getLoggedId());

    if (gallery && club <= 0)
      throw new HttpForbiddenException("insufficient_rights");

    if (isAllowedToCreateMedia(author, file.getSize())) {
      String name, mime = file.getContentType().split("/")[0];
      Media media;
      switch (mime) {
        case "video":
          media = new Video();
          ((Video) media).setRatio(ratio);
          name = fileHandler.upload(file, StorageConfig.MEDIAS_CONF.get("video").path, false,
            Map.of(
              "process", "compress"
            )
          );
          break;
        case "image":
          media = new Image();
          if(averageColor.length() > 6)
            throw new HttpBadRequestException("color_bad_format");
          ((Image) media).setColor(averageColor);
          ((Image) media).setRatio(ratio);
          ((Image) media).setThread(new Thread(ThreadType.MEDIA));
          if (gallery) {
            name = fileHandler.upload(file, StorageConfig.MEDIAS_CONF.get("gallery").path, false,
              Map.of(
                "process", "compress",
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
          if(file.getOriginalFilename().length() > 128)
            throw new HttpBadRequestException("file_name_too_long");
          
          Document doc;
          media = doc = new Document();
          name = fileHandler.upload(
            file,
            StorageConfig.MEDIAS_CONF.get("document").path+"/"+RandomString.generate(30)+"/"+file.getOriginalFilename(),
            true,
            Collections.EMPTY_MAP
          );
          doc.setTitle(file.getOriginalFilename());
          doc.setSizeBytes(file.getSize());
          break;
      }

      media.setName(name);
      media.setNSFW(nsfw);
      media.setCreation(new Date());
      return mediaRepository.save(media);
    }

    throw new MediaMaxUploadException("daily_upload_reached");
  }

  public boolean toggleNSFW(Long id) {
    Media media = getImage(id);
    media.setNSFW(!media.isNSFW());

    mediaRepository.save(media);
    return media.isNSFW();
  }

  void deleteMedia(Media media) {
    String name = media.getName();
    fileHandler.delete(name);
    mediaRepository.delete(media);
  }


  public List<Matched> getImageTags(Long id) {
    Image image = getImage(id);

    return image.getMatched();
  }

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


  public void tagStudentInImage(Long imageId, Long studentId, TokenPayload auth) {
    Image image = getImage(imageId);
    List<Matched> matchedList = matchedRepository.findAllByImage(image);
    int res = (int) matchedList.stream()
      .filter(m -> m.getMatch().getId().equals(studentId))
      .count();

    if (res > 0) {
      throw new HttpBadRequestException("already_tagged");
    }
    Student match = studentService.getStudent(studentId);
    Student owner = studentService.getStudent(auth.getId());
    Matched matched = new Matched();
    matched.setMatch(match);
    matched.setOwner(owner);
    matched.setImage(image);
    matchedRepository.save(matched);
  }


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
