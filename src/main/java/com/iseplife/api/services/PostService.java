package com.iseplife.api.services;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.dao.group.FeedRepository;
import com.iseplife.api.dao.post.*;
import com.iseplife.api.dto.PostDTO;
import com.iseplife.api.dto.PostUpdateDTO;
import com.iseplife.api.dto.view.AuthorView;
import com.iseplife.api.dto.view.PostView;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.post.embed.Embedable;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.post.embed.media.Media;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.constants.PublishStateEnum;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.media.MediaRepository;
import com.iseplife.api.dao.student.StudentRepository;
import com.iseplife.api.exceptions.AuthException;
import com.iseplife.api.exceptions.IllegalArgumentException;
import com.iseplife.api.websocket.PostMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

  @Autowired
  AuthService authService;

  @Autowired
  PostRepository postRepository;

  @Autowired
  ThreadRepository threadRepository;

  @Autowired
  FeedRepository feedRepository;

  @Autowired
  MediaRepository mediaRepository;

  @Autowired
  StudentRepository studentRepository;

  @Autowired
  PostFactory postFactory;

  @Autowired
  AuthorFactory authorFactory;

  @Autowired
  StudentService studentService;

  @Autowired
  ClubService clubService;

  @Autowired
  MediaService mediaService;

  @Autowired
  PollService pollService;

  @Autowired
  GalleryService galleryService;

  @Autowired
  FeedService feedService;

  @Autowired
  PostMessageService postMessageService;

  private final int POSTS_PER_PAGE = 10;


  private Post getPost(Long postID) {
    Optional<Post> post = postRepository.findById(postID);
    if (post.isEmpty())
      throw new IllegalArgumentException("Could not find this post (id:" + postID + ")");

    return post.get();
  }

  public PostView getPostView(Long postID) {
    Post post = getPost(postID);
    if (post.getPrivate()) {
      return null;
    }
    return postFactory.entityToView(post);
  }

  public PostView createPost(TokenPayload auth, PostDTO postDTO) {
    Post post = postFactory.dtoToEntity(postDTO);
    post.setAuthor(studentService.getStudent(auth.getId()));
    post.setLinkedClub(postDTO.getLinkedClub() != null ? clubService.getClub(postDTO.getLinkedClub()) : null);

    // if creator is not an ADMIN
    if (!auth.getRoles().contains(Roles.ADMIN)) {
      // Check if user is able to post in club's name
      if (post.getLinkedClub() != null && !auth.getClubsPublisher().contains(postDTO.getLinkedClub())) {
        throw new AuthException("not allowed to create this post");
      }
    }

    post.setFeed(feedService.getFeed(postDTO.getFeed()));
    post.setThread(new Thread());
    post.setCreationDate(new Date());
    post.setPublicationDate(postDTO.getPublicationDate() == null ? new Date() : postDTO.getPublicationDate());
    post.setPublishState(postDTO.getDraft() ? PublishStateEnum.WAITING : null);
    post = postRepository.save(post);

    postMessageService.broadcastPost(auth.getId(), post);
    return postFactory.entityToView(post);
  }

  public Post updatePost(Long postID, PostUpdateDTO update) {
    Post post = getPost(postID);
    if (!authService.hasRightOn(post)) {
      throw new AuthException("You have not sufficient rights on this post (id:" + postID + ")");
    }

    post.setDescription(update.getDescription());
    post.setPublicationDate(update.getPublicationDate());
    post.setPrivate(update.getPrivate());
    return postRepository.save(post);
  }

  public void deletePost(Long postID) {
    Post post = getPost(postID);
    if (!authService.hasRightOn(post)) {
      throw new AuthException("You have not sufficient rights on this post (id:" + postID + ")");
    }

    Embedable embed = post.getEmbed();
    if (embed instanceof Media) {
      mediaService.deleteMedia((Media) embed);
    }
    if (embed instanceof Gallery) {
      galleryService.deleteGallery((Gallery) embed);
    }

    postRepository.deleteById(postID);
  }


  public void togglePinnedPost(Long postID) {
    Post post = getPost(postID);
    if (authService.hasRightOn(post) && post.getLinkedClub() != null) {
      throw new AuthException("You have not sufficient rights on this post (id:" + postID + ")");
    }

    post.setPinned(!post.getPinned());
    postRepository.save(post);
  }

  public void setPublishState(Long postID, PublishStateEnum state) {
    Post post = getPost(postID);
    post.setPublishState(state);
    postRepository.save(post);
  }

  public void addMediaEmbed(Long postID, String type, Long embedID) {
    Post post = getPost(postID);
    Embedable embed;
    switch (type) {
      case EmbedType.GALLERY:
        embed = galleryService.getGallery(embedID);
        break;
      case EmbedType.POLL:
        embed = pollService.getPoll(embedID);
        break;
      case EmbedType.IMAGE:
      case EmbedType.VIDEO:
      case EmbedType.DOCUMENT:
        embed = mediaService.getMedia(embedID);
        break;
      default:
        throw new IllegalArgumentException("the embed type (" + type + ") doesn't exist");
    }

    post.setEmbed(embed);
    postRepository.save(post);
  }

  public Post addMediaEmbed(Long id, Embedable embed) {
    Post post = getPost(id);
    post.setEmbed(embed);

    return postRepository.save(post);
  }

  public Set<AuthorView> getAuthorizedPublish(TokenPayload auth) {
    Student student = studentService.getStudent(auth.getId());

    Set<AuthorView> authorStatus = new HashSet<>();
    authorStatus.add(authorFactory.entitytoView(student));

    if (auth.getRoles().contains(Roles.ADMIN)) {
      authorStatus.add(authorFactory.admintoView());
      authorStatus.addAll(
        clubService.getAll()
          .stream()
          .map(c -> authorFactory.entitytoView(c))
          .collect(Collectors.toSet())
      );
    } else {
      authorStatus.addAll(
        studentService.getPublisherClubs(student)
          .stream()
          .map(c -> authorFactory.entitytoView(c))
          .collect(Collectors.toSet())
      );
    }

    return authorStatus;
  }

  public Page<PostView> getFeedPosts(Feed feed, int page) {
    Page<Post> posts = postRepository.findByFeedAndPublishStateOrderByPublicationDate(feed,
      PublishStateEnum.PUBLISHED, PageRequest.of(page, POSTS_PER_PAGE));

    return posts.map(post -> postFactory.entityToView(post));
  }

  public List<PostView> getFeedPostsPinned(Feed feed) {
    List<Post> posts = postRepository.findByFeedAndIsPinnedIsTrue(feed);

    return posts.stream().map(post -> postFactory.entityToView(post)).collect(Collectors.toList());
  }

  public List<PostView> getFeedDrafts(Feed feed, Student author) {
    List<Post> posts = postRepository.findFeedDrafts(feed, author);

    return posts.stream().map(post -> postFactory.entityToView(post)).collect(Collectors.toList());
  }

  public List<PostView> getFeedPostsWaiting(Feed feed) {
    List<Post> posts = postRepository.findByFeedAndPublishStateOrderByPublicationDate(feed, PublishStateEnum.WAITING);

    return posts.stream().map(post -> postFactory.entityToView(post)).collect(Collectors.toList());
  }

  public Page<PostView> getPostsAuthor(Long id, boolean isPublic, int page) {
    if (isPublic) {
      Page<Post> posts = postRepository.findByAuthorIdAndIsPrivateOrderByCreationDateDesc(id, false, PageRequest.of(page, POSTS_PER_PAGE));
      return posts.map(p -> postFactory.entityToView(p));
    }
    Page<Post> posts = postRepository.findByAuthorIdOrderByCreationDateDesc(id, PageRequest.of(page, POSTS_PER_PAGE));
    return posts.map(p -> postFactory.entityToView(p));
  }
}
