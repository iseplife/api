package com.iseplife.api.services;

import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.MediaType;
import com.iseplife.api.dao.GalleryRepository;
import com.iseplife.api.dto.view.MatchedView;
import com.iseplife.api.entity.post.embed.media.Image;
import com.iseplife.api.entity.Matched;
import com.iseplife.api.entity.post.embed.media.Document;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.entity.post.embed.media.Media;
import com.iseplife.api.entity.post.embed.media.Video;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.constants.PublishStateEnum;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.media.image.ImageRepository;
import com.iseplife.api.dao.media.image.MatchedRepository;
import com.iseplife.api.dao.media.MediaRepository;
import com.iseplife.api.dao.post.PostRepository;
import com.iseplife.api.exceptions.IllegalArgumentException;
import com.iseplife.api.utils.MediaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.Cacheable;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@Service
public class MediaService {

  private final Logger LOG = LoggerFactory.getLogger(MediaService.class);

  @Autowired
  MediaUtils mediaUtils;

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
  PostService postService;

  @Autowired
  StudentService studentService;

  @Autowired
  FileHandler fileHandler;

  @Value("${storage.image.url}")
  String imageDir;

  @Value("${storage.video.url}")
  String videoDir;

  @Value("${storage.document.url}")
  String documentDir;

  private static final int ALL_MEDIA_PAGE_SIZE = 20;

  private static final int PHOTOS_PER_PAGE = 30;


  @Cacheable("media-list-published")
  public Page<Media> getAllGalleryGazetteVideoPublished(int page) {
    Page<Media> list = mediaRepository.findAllByMediaTypeInOrderByCreationDesc(
      Arrays.asList(MediaType.IMAGE, MediaType.VIDEO),
      new PageRequest(page, ALL_MEDIA_PAGE_SIZE)
    );

    return list;
  }

  @Cacheable("media-list-all")
  public Page<Media> getAllGalleryGazetteVideo(int page) {
    return mediaRepository.findAllByMediaTypeInOrderByCreationDesc(
      Arrays.asList(MediaType.IMAGE, MediaType.VIDEO),
      new PageRequest(page, ALL_MEDIA_PAGE_SIZE)
    );
  }

  private Image getImage(Long id) {
    Image img = imageRepository.findOne(id);
    if (img != null) {
      return img;
    }
    throw new RuntimeException("could not get the image with id: " + id);
  }

  public Document createDocument(Long postId, MultipartFile fileUploaded) {
    Document document = new Document();
    document.setCreation(new Date());

    String name = fileHandler.upload(fileUploaded, ObjectUtils.asMap("folder", "document"));
    document.setName(name);
    document.setOriginalName(fileUploaded.getOriginalFilename());
    document = mediaRepository.save(document);

    postService.addMediaEmbed(postId, document);
    postService.setPublishState(postId, PublishStateEnum.PUBLISHED);
    return document;
  }


  public Video uploadVideo(Long postId, String title, MultipartFile videoFile) {
    Video video = new Video();
    video.setCreation(new Date());
    video.setTitle(title);

    String name = fileHandler.upload(
      videoFile,
      ObjectUtils.asMap(
        "folder", "post",
        "async", true,
        "eager", Collections.singletonList(new Transformation().audioCodec("aac").videoCodec("h264"))
      ));
    video.setName(name);

    Video savedVideo = mediaRepository.save(video);
    Post post = postService.addMediaEmbed(postId, video);

    return savedVideo;
  }




  public boolean toggleNSFW(Long id) {
    Media media = mediaRepository.findOne(id);
    media.setNSFW(!media.isNSFW());

    mediaRepository.save(media);
    return media.isNSFW();
  }


  /**
   * Add a single image
   *
   * @param postID
   * @param file
   * @return
   */
  public Image addSingleImage(Long postID, MultipartFile file) {
    Image image = new Image();
    image.setCreation(new Date());

    String name = fileHandler.upload(file, ObjectUtils.asMap("folder", "post"));
    image.setName(name);
    image = mediaRepository.save(image);

    postService.addMediaEmbed(postID, image);
    postService.setPublishState(postID, PublishStateEnum.PUBLISHED);
    return image;
  }

  public Image addGalleryImage(MultipartFile file, Gallery gallery) {
    Image image = new Image();
    image.setGallery(gallery);

    String name = fileHandler.upload(file, ObjectUtils.asMap("folder", "post/" + gallery.getId()));
    image.setName(name);

    return image;
  }

  public void addGalleryImage(File file, Gallery gallery) {
    Image image = new Image();
    image.setGallery(gallery);
    image.setCreation(new Date());

    String name = fileHandler.upload(file, ObjectUtils.asMap("folder", "post/" + gallery.getId()));
    image.setName(name);

    mediaRepository.save(image);
    if (!file.delete()) {
      LOG.error("Could not delete this temp file: {}", file.getName());
    }
  }

  void deleteImageFile(Image image) {
    fileHandler.delete(image.getName(), ObjectUtils.emptyMap());
    imageRepository.delete(image);
  }

  /**
   * Get all people linked to an image
   *
   * @param id
   * @return
   */
  public List<Matched> getImageTags(Long id) {
    Image image = imageRepository.findOne(id);
    if (image == null) {
      throw new IllegalArgumentException("could not find this image");
    }
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
    return matchedRepository.findAllByMatchId(studentId, new PageRequest(page, PHOTOS_PER_PAGE)).map(m -> {
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
