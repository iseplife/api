package com.iseplife.api.services;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.dao.post.*;
import com.iseplife.api.dto.post.PostCreationDTO;
import com.iseplife.api.dto.PostUpdateDTO;
import com.iseplife.api.dto.view.AuthorView;
import com.iseplife.api.dto.post.view.PostView;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.post.embed.Embedable;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.post.embed.media.Media;
import com.iseplife.api.entity.post.embed.poll.Poll;
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

  public Post getPostFromEmbed(Embedable embed) {
    Optional<Post> post = postRepository.findByEmbed(embed);
    if (post.isEmpty())
      throw new IllegalArgumentException("Could not find a post related to this embed)");

    return post.get();
  }

  public PostView getPostView(Long postID) {
    Post post = getPost(postID);
    if (post.getPrivate()) {
      return null;
    }
    return postFactory.entityToView(post);
  }

  public PostView createPost(PostCreationDTO dto) {
    Post post = postFactory.dtoToEntity(dto);
    Feed feed = feedService.getFeed(dto.getFeed());

    if (!SecurityService.hasRightOn(feed))
      throw new AuthException("You are not allow to create a post here");

    post.setFeed(feed);
    post.setAuthor(securityService.getLoggedUser());

    // Author should be an admin or club publisher
    if (dto.getLinkedClub() != null && !SecurityService.hasAuthorAccessOn(dto.getLinkedClub()))
      throw new AuthException("insufficient rights");

    post.setLinkedClub(dto.getLinkedClub() != null ? clubService.getClub(dto.getLinkedClub()) : null);

    dto.getAttachements().forEach((type, id) -> bindAttachementToPost(type, id, post));

    post.setThread(new Thread());
    post.setCreationDate(new Date());
    post.setPublicationDate(dto.getPublicationDate() == null ? new Date() : dto.getPublicationDate());
    post.setState(dto.isDraft() ? PostState.DRAFT : PostState.READY);

    return postFactory.entityToView(postRepository.save(post));
  }

  public PostView updatePost(Long postID, PostUpdateDTO dto) {
    Post post = getPost(postID);
    if (!SecurityService.hasRightOn(post)) {
      throw new AuthException("You have not sufficient rights on this post (id:" + postID + ")");
    }

    // Author should be an admin or club publisher
    if (dto.getLinkedClub() != null && !SecurityService.hasAuthorAccessOn(dto.getLinkedClub()))
      throw new AuthException("insufficient rights");

    post.setLinkedClub(dto.getLinkedClub() != null ? clubService.getClub(dto.getLinkedClub()) : null);

    post.setDescription(dto.getDescription());
    post.setPublicationDate(dto.getPublicationDate());
    post.setPrivate(dto.getPrivate());

    if (!dto.getAttachements().isEmpty()) {
      removeEmbed(post.getEmbed());
      dto.getAttachements().forEach((type, id) -> bindAttachementToPost(type, id, post));
    }

    if (dto.isRemoveEmbed()) {
      removeEmbed(post.getEmbed());
      post.setEmbed(null);
    }

    return postFactory.entityToView(postRepository.save(post));
  }

  private void removeEmbed(Embedable embed) {
    if (embed != null) {
      switch (embed.getEmbedType()) {
        case EmbedType.IMAGE:
          galleryService.deleteGallery((Gallery) embed);
          break;
        case EmbedType.POLL:
          pollService.deletePoll((Poll) embed);
          break;
        case EmbedType.VIDEO:
        case EmbedType.DOCUMENT:
          mediaService.deleteMedia((Media) embed);
          break;
      }
    }
  }

  public void deletePost(Long postID) {
    Post post = getPost(postID);
    if (!SecurityService.hasRightOn(post))
      throw new AuthException("You have not sufficient rights on this post (id:" + postID + ")");

    Embedable embed = post.getEmbed();
    if (embed != null)
      removeEmbed(embed);

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


  private void bindAttachementToPost(String type, Long id, Post post) {
    Embedable attachement;
    switch (type) {
      case EmbedType.IMAGE:
      case EmbedType.GALLERY:
        attachement = galleryService.getGallery(id);
        break;
      case EmbedType.POLL:
        attachement = pollService.getPoll(id);
        ((Poll) attachement).setFeed(post.getFeed());
        break;
      case EmbedType.VIDEO:
      case EmbedType.DOCUMENT:
        attachement = mediaService.getMedia(id);
        break;
      default:
        throw new IllegalArgumentException("embed type (" + type + ") doesn't exist");
    }
    post.setEmbed(attachement);
  }

  public Set<AuthorView> getAuthorizedPublish(TokenPayload auth, Boolean clubOnly) {
    Student student = studentService.getStudent(auth.getId());
    Set<AuthorView> authorStatus = new HashSet<>();

    if (auth.getRoles().contains(Roles.ADMIN)) {
      if (!clubOnly)
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
    Page<Post> posts = postRepository.findByFeedAndStateOrderByPublicationDateDesc(
      feed,
      PostState.READY,
      PageRequest.of(page, POSTS_PER_PAGE)
    );

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
