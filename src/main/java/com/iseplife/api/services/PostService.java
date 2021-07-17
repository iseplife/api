package com.iseplife.api.services;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.dao.post.*;
import com.iseplife.api.dto.post.PostDTO;
import com.iseplife.api.dto.PostUpdateDTO;
import com.iseplife.api.dto.view.AuthorView;
import com.iseplife.api.dto.post.view.PostView;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.post.embed.Embedable;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.post.embed.media.Media;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.constants.PostState;
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
  PostRepository postRepository;

  @Autowired
  ThreadRepository threadRepository;

  @Autowired
  MediaRepository mediaRepository;

  @Autowired
  StudentRepository studentRepository;

  @Autowired
  PostFactory postFactory;

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
  SecurityService securityService;

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
    Feed feed = feedService.getFeed(postDTO.getFeed());

    if(!SecurityService.hasRightOn(feed))
      throw new AuthException("You are not allow to create a post here");

    post.setFeed(feed);
    post.setAuthor(securityService.getLoggedUser());
    post.setLinkedClub(postDTO.getLinkedClub() != null ? clubService.getClub(postDTO.getLinkedClub()) : null);

    // Author should be an admin or club publisher
    if (!auth.getRoles().contains(Roles.ADMIN)) {
      if (post.getLinkedClub() != null && !auth.getClubsPublisher().contains(postDTO.getLinkedClub())) {
        throw new AuthException("not allowed to create this post");
      }
    }

    postDTO.getAttachements().forEach((type, id) -> {
      Embedable attachement;
      switch (type){
        case EmbedType.GALLERY:
          attachement = galleryService.getGallery(id);
          break;
        case EmbedType.POLL:
          attachement = pollService.bindPollToPost(id, feed);
          break;
        case EmbedType.VIDEO:
        case EmbedType.DOCUMENT:
        case EmbedType.IMAGE:
          attachement = mediaService.getMedia(id);
          break;
        default:
          throw new IllegalArgumentException("Invalid attachments");
      }
      post.setEmbed(attachement);
    });

    post.setThread(new Thread());
    post.setCreationDate(new Date());
    post.setPublicationDate(postDTO.getPublicationDate() == null ? postDTO.getPublicationDate(): new Date());
    post.setState(postDTO.isDraft() ? PostState.DRAFT : PostState.READY);

    return postFactory.entityToView(postRepository.save(post));
  }

  public Post updatePost(Long postID, PostUpdateDTO update) {
    Post post = getPost(postID);
    if (!SecurityService.hasRightOn(post)) {
      throw new AuthException("You have not sufficient rights on this post (id:" + postID + ")");
    }

    post.setDescription(update.getDescription());
    post.setPublicationDate(update.getPublicationDate());
    post.setPrivate(update.getPrivate());
    return postRepository.save(post);
  }

  public void deletePost(Long postID) {
    Post post = getPost(postID);
    if (!SecurityService.hasRightOn(post))
      throw new AuthException("You have not sufficient rights on this post (id:" + postID + ")");

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
    if (SecurityService.hasRightOn(post) && post.getLinkedClub() != null) {
      throw new AuthException("You have not sufficient rights on this post (id:" + postID + ")");
    }

    post.setPinned(!post.getPinned());
    postRepository.save(post);
  }

  public void setPublishState(Long postID, PostState state) {
    Post post = getPost(postID);
    post.setState(state);
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

  public Set<AuthorView> getAuthorizedPublish(TokenPayload auth, Boolean clubOnly) {
    Student student = studentService.getStudent(auth.getId());
    Set<AuthorView> authorStatus = new HashSet<>();

    if (auth.getRoles().contains(Roles.ADMIN)) {
      if(!clubOnly)
        authorStatus.add(AuthorFactory.adminToView());

      authorStatus.addAll(
        clubService.getAll()
          .stream()
          .map(AuthorFactory::toView)
          .collect(Collectors.toSet())
      );
    } else {
      authorStatus.addAll(
        studentService.getPublisherClubs(student)
          .stream()
          .map(AuthorFactory::toView)
          .collect(Collectors.toSet())
      );
    }

    return authorStatus;
  }

  public Page<PostView> getMainPosts(int page) {
    Page<Post> posts = postRepository.findMainPostsByState(PostState.READY, PageRequest.of(page, POSTS_PER_PAGE));

    return posts.map(post -> postFactory.entityToView(post));
  }

  public Page<PostView> getFeedPosts(Feed feed, int page) {
    Page<Post> posts = postRepository.findByFeedAndStateOrderByPublicationDateDesc(feed,
      PostState.READY, PageRequest.of(page, POSTS_PER_PAGE));

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
    List<Post> posts = postRepository.findByFeedAndStateOrderByPublicationDateDesc(feed, PostState.WAITING);

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
