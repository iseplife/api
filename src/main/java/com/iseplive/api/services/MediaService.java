package com.iseplive.api.services;

import com.iseplive.api.conf.jwt.TokenPayload;
import com.iseplive.api.constants.MediaType;
import com.iseplive.api.constants.PublishStateEnum;
import com.iseplive.api.constants.Roles;
import com.iseplive.api.dao.image.ImageRepository;
import com.iseplive.api.dao.image.MatchedRepository;
import com.iseplive.api.dao.media.MediaRepository;
import com.iseplive.api.dao.post.PostRepository;
import com.iseplive.api.dto.TempFile;
import com.iseplive.api.dto.view.MatchedView;
import com.iseplive.api.entity.Image;
import com.iseplive.api.entity.Matched;
import com.iseplive.api.entity.Post;
import com.iseplive.api.entity.media.*;
import com.iseplive.api.entity.user.Student;
import com.iseplive.api.exceptions.FileException;
import com.iseplive.api.exceptions.IllegalArgumentException;
import com.iseplive.api.utils.MediaUtils;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Created by Guillaume on 01/08/2017.
 * back
 */
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
      Arrays.asList(MediaType.GALLERY, MediaType.VIDEO), false, PublishStateEnum.PUBLISHED, new PageRequest(page, ALL_MEDIA_PAGE_SIZE)
    );
  }

  /**
   * List all galleries, gazette and video published
   * @param page
   */
  @Cacheable("media-list-published")
  public Page<Media>getAllGalleryGazetteVideoPublished(int page) {
    return mediaRepository.findAllByMediaTypeInAndPost_PublishStateOrderByCreationDesc(
      Arrays.asList(MediaType.GALLERY, MediaType.VIDEO), PublishStateEnum.PUBLISHED, new PageRequest(page, ALL_MEDIA_PAGE_SIZE)
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
      Arrays.asList(MediaType.GALLERY, MediaType.VIDEO), new PageRequest(page, ALL_MEDIA_PAGE_SIZE)
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
    postService.addMediaEmbed(postId, document.getId());
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
    Post post = postService.addMediaEmbed(postId, video.getId());

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

  /**
   * Add a single image
   * @param postId
   * @param file
   * @return
   */
  public Image addSingleImage(Long postId, MultipartFile file) {
    Image image = imageRepository.save(addImage(file, null));
    postService.addMediaEmbed(postId, image.getId());
    postService.setPublishState(postId, PublishStateEnum.PUBLISHED);
    return image;
  }

  /**
   * Add an image to a gallery after upload
   * @param file
   * @param gallery
   * @return
   */
  private Image addImage(MultipartFile file, Gallery gallery) {
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
  private Image addImage(File file, String contentType, Gallery gallery) {
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

    return image;
  }

  /**
   * Delete all files attached to an image on disk
   * @param image
   */
  void deleteImageFile(Image image) {
    mediaUtils.removeIfExistPublic(image.getThumbUrl());
    mediaUtils.removeIfExistPublic(image.getFullSizeUrl());
    mediaUtils.removeIfExistPublic(image.getOriginalUrl());
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
    int res = matchedList.stream()
      .filter(m -> m.getMatch().getId().equals(studentId))
      .collect(Collectors.toList()).size();
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

  /**
   * Create a new gallery
   * @param postId
   * @param name
   * @param files
   * @return
   */
  public Gallery createGallery(Long postId, String name, List<MultipartFile> files) {
    Gallery gallery = new Gallery();
    gallery.setName(name);
    gallery.setCreation(new Date());

    Gallery galleryRes = mediaRepository.save(gallery);
    postService.addMediaEmbed(postId, galleryRes.getId());

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
        imageRepository.save(addImage(file.getFile(), file.getContentType(), galleryRes));
        if (!file.getFile().delete()) {
          LOG.error("could not delete this temp file: {}", file.getFile().getName());
        }
      });
      postService.setPublishState(postId, PublishStateEnum.PUBLISHED);
    });

    return galleryRes;
  }

  /**
   * Get a gallery by ID
   * @param id
   * @return
   */
  public Gallery getGallery(Long id) {
    Media media = mediaRepository.findOne(id);
    if (media instanceof Gallery) {
      return (Gallery) media;
    }
    throw new IllegalArgumentException("Could not find gallery with id: "+id);
  }

  /**
   * Get all images from a gallery
   * @param id
   * @return
   */
  public List<Image> getGalleryImages(Long id) {
    Gallery gallery = getGallery(id);
    return gallery.getImages();
  }

  /**
   * Delete list of images
   * @param imagesIds
   * @param imagesIds
   */
  public void deleteImagesGallery(Long galleryId, List<Long> imagesIds) {
    long imageNb = getGallery(galleryId).getImages().stream()
      .filter(img -> imagesIds.contains(img.getId()))
      .count();
    if (imagesIds.size() != imageNb) {
      throw new IllegalArgumentException("images does not belong to this gallery");
    }
    List<Image> images = imageRepository.findImageByIdIn(imagesIds);
    images.forEach(img -> {
      deleteImageFile(img);
      imageRepository.delete(img);
    });
  }

  /**
   * Add images in a gallery
   * @param gallery
   * @param files
   */
  public void addImagesGallery(Gallery gallery, List<MultipartFile> files) {
    List<Image> images = new ArrayList<>();
    files.forEach(file -> images.add(addImage(file, gallery)));
    imageRepository.save(images);
  }
}
