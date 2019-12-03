package com.iseplife.api.services;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.MediaType;
import com.iseplife.api.dto.view.MatchedView;
import com.iseplife.api.entity.media.Image;
import com.iseplife.api.entity.Matched;
import com.iseplife.api.entity.post.embed.Document;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.entity.media.Media;
import com.iseplife.api.entity.media.Video;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.constants.PublishStateEnum;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.media.image.ImageRepository;
import com.iseplife.api.dao.media.image.MatchedRepository;
import com.iseplife.api.dao.media.MediaRepository;
import com.iseplife.api.dao.post.PostRepository;
import com.iseplife.api.exceptions.FileException;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;


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
  PostService postService;

  @Autowired
  StudentService studentService;

  @Value("${storage.image.url}")
  String imageDir;

  @Value("${storage.video.url}")
  String videoDir;



  @Value("${storage.document.url}")
  String documentDir;

  private static final int ALL_MEDIA_PAGE_SIZE = 20;

  private static final int WIDTH_IMAGE_SIZE = 1280;
  private static final int WIDTH_IMAGE_SIZE_THUMB = 768;

  private static final int PHOTOS_PER_PAGE = 30;

  /**
   * List all galleries, gazette and video published in a public post
   * @param page
   * @return
   */
  public Page<Media> getAllGalleryGazetteVideoPublic(int page) {
    return mediaRepository.findAllByMediaTypeInAndPost_isPrivateAndPost_PublishStateOrderByCreationDesc(
      Arrays.asList(EmbedType.GALLERY, MediaType.VIDEO), false, PublishStateEnum.PUBLISHED, new PageRequest(page, ALL_MEDIA_PAGE_SIZE)
    );
  }

  /**
   * List all galleries, gazette and video published
   * @param page
   */
  @Cacheable("media-list-published")
  public Page<Media>getAllGalleryGazetteVideoPublished(int page) {
    return mediaRepository.findAllByMediaTypeInAndPost_PublishStateOrderByCreationDesc(
      Arrays.asList(EmbedType.GALLERY, MediaType.VIDEO), PublishStateEnum.PUBLISHED, new PageRequest(page, ALL_MEDIA_PAGE_SIZE)
    );
  }

  /**
   * List all galleries, gazette and video published or not
   * @param page
   * @return
   */
  @Cacheable("media-list-all")
  public Page<Media> getAllGalleryGazetteVideo(int page) {
    return mediaRepository.findAllByMediaTypeInOrderByCreationDesc(
      Arrays.asList(EmbedType.GALLERY, MediaType.VIDEO), new PageRequest(page, ALL_MEDIA_PAGE_SIZE)
    );
  }

  /**
   * Create a document
   * @param postId
   * @param name
   * @param fileUploaded
   * @return
   */
  public Document createDocument(Long postId, String name, MultipartFile fileUploaded) {
    Document document = new Document();
    document.setCreation(new Date());

    String random = mediaUtils.randomName();
    String documentPath = String.format(
      "%s_%s",
      mediaUtils.resolvePath(documentDir, random,false, document.getCreation()),
      fileUploaded.getOriginalFilename()
    );

    mediaUtils.saveFile(documentPath, fileUploaded);

    document.setName(name);
    document.setOriginalName(fileUploaded.getOriginalFilename());
    document.setPath(mediaUtils.getPublicUrl(documentPath));
    document = mediaRepository.save(document);

    postService.addMediaEmbed(postId, document);
    postService.setPublishState(postId, PublishStateEnum.PUBLISHED);
    return document;
  }

  /**
   * Upload a video and compress it
   * @param postId
   * @param name
   * @param videoFile
   * @return
   */
  public Video uploadVideo(Long postId, String name, MultipartFile videoFile) {
    String random = mediaUtils.randomName();
    String videoPath = String.format(
      "%s_%s.mp4",
      mediaUtils.resolvePath(videoDir, random, false),
      videoFile.getName()
    );

    String thumbnailPath = String.format(
      "%s_%s.png",
      mediaUtils.resolvePath(videoDir, random, false),
      videoFile.getName()
    );

    Video video = new Video();
    video.setCreation(new Date());
    video.setName(name);
    video.setUrl(mediaUtils.getPublicUrl(videoPath));
    Video savedVideo = mediaRepository.save(video);
    Post post = postService.addMediaEmbed(postId, video);

    try {
      // temporary store video before compressing it
      Path copyVid = Files.createTempFile(name, ".tmp");
      // the file should be deleted when the JVM exits to free space.
      copyVid.toFile().deleteOnExit();
      Files.copy(videoFile.getInputStream(), copyVid, StandardCopyOption.REPLACE_EXISTING);

      CompletableFuture.runAsync(() -> {
        try {
          // generate thumbnail from video
          mediaUtils.generateVideoThumbnail(copyVid, thumbnailPath);
          savedVideo.setPoster(mediaUtils.getPublicUrl(thumbnailPath));
          mediaRepository.save(savedVideo);

          // then compress video
          mediaUtils.compressVideo(copyVid, videoPath);
          if (!copyVid.toFile().delete()) {
            LOG.error("could not delete temp file");
          }

          post.setPublishState(PublishStateEnum.PUBLISHED);
          postRepository.save(post);
        } catch (IOException e) {
          LOG.error("could not compress video and generate thumbnail", e);
        }
      });

    } catch (IOException e) {
      LOG.error("could not create temp video", e);
      throw new FileException("could not create temp video");
    }

    //mediaUtils.saveFile(videoPath, videoFile);

    return savedVideo;
  }

  /**
   * get an image by ID
   * @param id
   * @return
   */
  private Image getImage(Long id) {
    Image img = imageRepository.findOne(id);
    if (img != null) {
      return img;
    }
    throw new RuntimeException("could not get the image with id: " + id);
  }

  public boolean toggleNSFW(Long id) {
    Media media = mediaRepository.findOne(id);
    media.setNSFW(!media.isNSFW());

    mediaRepository.save(media);
    return media.isNSFW();
  }

  private void uploadFile(File file){

  }

  private void uploadFile(MultipartFile file){

  }

  /**
   * Add a single image
   * @param postID
   * @param file
   * @return
   */
  public Image addSingleImage(Long postID, MultipartFile file) {
    Image image = new Image();
    String name = mediaUtils.randomName();

    String pathOriginal = String.format(
      "%s_%s",
      mediaUtils.resolvePath(imageDir, name, false, new Date()),
      file.getOriginalFilename().replaceAll(" ", "-")
    );
    File originalFile = mediaUtils.saveFile(pathOriginal, file);

    String path = mediaUtils.resolvePath(imageDir, name, false, new Date());
    mediaUtils.saveJPG(originalFile, file.getContentType(), WIDTH_IMAGE_SIZE, path);

    String pathThumb = mediaUtils.resolvePath(imageDir, name, true, new Date());
    mediaUtils.saveJPG(originalFile, file.getContentType(), WIDTH_IMAGE_SIZE_THUMB, pathThumb);


    image.setFullSizeUrl(mediaUtils.getPublicUrlImage(path));
    image.setThumbUrl(mediaUtils.getPublicUrlImage(pathThumb));
    image.setOriginalUrl(mediaUtils.getPublicUrl(pathOriginal));

    image = mediaRepository.save(image);

    postService.addMediaEmbed(postID, image);
    postService.setPublishState(postID, PublishStateEnum.PUBLISHED);
    return image;
  }

  /**
   * Add an image to a gallery after upload
   * @param file
   * @param gallery
   * @return
   */
  public Image addGalleryImage(MultipartFile file, Gallery gallery) {
    Image image = new Image();
    image.setGallery(gallery);

    String name = mediaUtils.randomName();

    String pathOriginal = String.format(
      "%s_%s",
      mediaUtils.resolvePath(imageDir, name, false, new Date()),
      file.getOriginalFilename().replaceAll(" ", "-")
    );
    File originalFile = mediaUtils.saveFile(pathOriginal, file);

    String path = mediaUtils.resolvePath(imageDir, name, false, new Date());
    mediaUtils.saveJPG(originalFile, file.getContentType(), WIDTH_IMAGE_SIZE, path);

    String pathThumb = mediaUtils.resolvePath(imageDir, name, true, new Date());
    mediaUtils.saveJPG(originalFile, file.getContentType(), WIDTH_IMAGE_SIZE_THUMB, pathThumb);


    image.setFullSizeUrl(mediaUtils.getPublicUrlImage(path));
    image.setThumbUrl(mediaUtils.getPublicUrlImage(pathThumb));
    image.setOriginalUrl(mediaUtils.getPublicUrl(pathOriginal));
    return image;
  }

  /**
   * Add an image to a gallery from a file
   * @param file
   * @param contentType
   * @param gallery
   * @return
   */
  public void addGalleryImage(File file, String contentType, Gallery gallery) {
    Image image = new Image();
    image.setGallery(gallery);

    String name = mediaUtils.randomName();
    String path = mediaUtils.resolvePath(imageDir, name, false, new Date());
    String pathThumb = mediaUtils.resolvePath(imageDir, name, true, new Date());

    String pathOriginal = String.format(
      "%s.%s",
      mediaUtils.resolvePath(imageDir, "original-"+name, false, new Date()),
      contentType.equals("image/jpeg") ? "jpg": contentType.equals("image/png") ? "png":""
    );

    mediaUtils.saveFile(pathOriginal, file);
    mediaUtils.saveJPG(file, contentType, WIDTH_IMAGE_SIZE, path);
    mediaUtils.saveJPG(file, contentType, WIDTH_IMAGE_SIZE_THUMB, pathThumb);

    image.setFullSizeUrl(mediaUtils.getPublicUrlImage(path));
    image.setThumbUrl(mediaUtils.getPublicUrlImage(pathThumb));
    image.setOriginalUrl(mediaUtils.getPublicUrl(pathOriginal));

    imageRepository.save(image);
    if (!file.delete()) {
      LOG.error("Could not delete this temp file: {}", file.getName());
    }
  }

  /**
   * Delete all files attached to an image on disk
   * @param image
   */
  void deleteImageFile(Image image) {
    mediaUtils.removeIfExistPublic(image.getThumbUrl());
    mediaUtils.removeIfExistPublic(image.getFullSizeUrl());
    mediaUtils.removeIfExistPublic(image.getOriginalUrl());

    imageRepository.delete(image);
  }

  /**
   * Get all people linked to an image
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
