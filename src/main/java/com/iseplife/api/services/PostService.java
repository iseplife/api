package com.iseplife.api.services;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dao.feed.FeedRepository;
import com.iseplife.api.dao.post.*;
import com.iseplife.api.dto.PostDTO;
import com.iseplife.api.dto.PostUpdateDTO;
import com.iseplife.api.dto.view.AuthorView;
import com.iseplife.api.dto.view.PostView;
import com.iseplife.api.entity.Feed;
import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.media.Document;
import com.iseplife.api.entity.media.Gallery;
import com.iseplife.api.entity.media.Media;
import com.iseplife.api.entity.media.Video;
import com.iseplife.api.entity.post.Comment;
import com.iseplife.api.entity.post.Like;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.constants.PublishStateEnum;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.media.MediaRepository;
import com.iseplife.api.dao.student.StudentRepository;
import com.iseplife.api.exceptions.AuthException;
import com.iseplife.api.exceptions.IllegalArgumentException;
import com.iseplife.api.utils.MediaUtils;
import com.iseplife.api.websocket.PostMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import springfox.documentation.annotations.Cacheable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Guillaume on 28/07/2017.
 * back
 */
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
  PostFactory postFactory;

  @Autowired
  AuthorFactory authorFactory;

  @Autowired
  LikeRepository likeRepository;

  @Autowired
  CommentFactory commentFactory;

  @Autowired
  CommentRepository commentRepository;

  @Autowired
  StudentService studentService;

  @Autowired
  MediaRepository mediaRepository;

  @Autowired
  StudentRepository studentRepository;

  @Autowired
  ClubService clubService;

  @Autowired
  MediaService mediaService;

  @Autowired
  PostMessageService postMessageService;

  @Autowired
  MediaUtils mediaUtils;

  private final int POSTS_PER_PAGE = 10;

  public Pageable createPage(int page) {
    return new PageRequest(page, POSTS_PER_PAGE);
  }

  @Cacheable("posts")
  public Page<PostView> getPosts(int page) {
    Page<Post> posts = postRepository.findByPublishStateAndIsPinnedOrderByCreationDateDesc(
      PublishStateEnum.PUBLISHED, false, createPage(page));
    return posts.map(post -> postFactory.entityToView(post));
  }


  public Page<PostView> getFeedPosts(Feed feed, int page) {
    Page<Post> posts = postRepository.findByFeedAndPublishStateOrderByPublicationDate(feed, PublishStateEnum.PUBLISHED, createPage(page));

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

  public Page<PostView> getPublicPosts(int page) {
    Page<Post> posts = postRepository.findByPublishStateAndIsPinnedAndIsPrivateOrderByCreationDateDesc(
      PublishStateEnum.PUBLISHED, false, false, createPage(page));
    return posts.map(post -> postFactory.entityToView(post));
  }

  @Cacheable("pinned-posts")
  public List<PostView> getPinnedPosts() {
    List<Post> posts = postRepository.findByPublishStateAndIsPinnedOrderByCreationDateDesc(
      PublishStateEnum.PUBLISHED, true);
    return posts.stream().map(post -> postFactory.entityToView(post)).collect(Collectors.toList());
  }

  public List<PostView> getPublicPinnedPosts() {
    List<Post> posts = postRepository.findByPublishStateAndIsPinnedAndIsPrivateOrderByCreationDateDesc(
      PublishStateEnum.PUBLISHED, true, false);
    return posts.stream().map(post -> postFactory.entityToView(post)).collect(Collectors.toList());
  }

  public Post createPost(TokenPayload auth, PostDTO postDTO) {
    Post post = postFactory.dtoToEntity(postDTO);
    post.setAuthor(studentRepository.findOne(auth.getId()));
    post.setLinkedClub(clubService.getClub(postDTO.getLinkedClubId()));

    // if creator is not an ADMIN
    if (!auth.getRoles().contains(Roles.ADMIN)) {
      // Check if user is able to post in club's name
      if (post.getLinkedClub() != null && !auth.getClubsPublisher().contains(postDTO.getLinkedClubId())) {
        throw new AuthException("not allowed to create this post");
      }
    }

    post.setFeed(feedRepository.findByName(postDTO.getFeed()));
    post.setThread(new Thread());
    post.setCreationDate(new Date());
    post.setPublishState(postDTO.isDraft() ? PublishStateEnum.WAITING : null);
    post = postRepository.save(post);

    postMessageService.broadcastPost(auth.getId(), post);
    return post;
  }



  public void deletePost(Long postId, TokenPayload auth) {
    Post post = getPost(postId);
    if (hasRightOnPost(auth, post)) {
      throw new AuthException("you cannot delete this post");
    }

    // delete media files on disk for each media type

    if (post.getMedia() instanceof Gallery) {
      Gallery gallery = (Gallery) post.getMedia();
      gallery.getImages().forEach(img -> mediaService.deleteImageFile(img));
    }

    if (post.getMedia() instanceof Document) {
      Document document = (Document) post.getMedia();
      mediaUtils.removeIfExistPublic(document.getPath());
    }

    if (post.getMedia() instanceof Video) {
      Video video = (Video) post.getMedia();
      if (video.getUrl() != null) {
        mediaUtils.removeIfExistPublic(video.getUrl());
      }
      if (video.getPoster() != null) {
        mediaUtils.removeIfExistPublic(video.getPoster());
      }
    }

    postRepository.delete(postId);
  }

  public Post addMediaEmbed(Long id, Long mediaId) {
    Post post = postRepository.findOne(id);
    Media media = mediaRepository.findOne(mediaId);
    post.setMedia(media);
    return postRepository.save(post);
  }


  public void setPublishState(Long id, PublishStateEnum state) {
    Post post = postRepository.findOne(id);
    post.setPublishState(state);
    postRepository.save(post);
  }



  public Post getPost(Long postId) {
    Post post = postRepository.findOne(postId);
    if (post == null) {
      throw new IllegalArgumentException("Could not find a post with id: " + postId);
    }
    return post;
  }

  public void setPinnedPost(Long id, Boolean pinned, TokenPayload auth) {
    Post post = getPost(id);
    boolean canPinPost = auth.getRoles().contains(Roles.ADMIN);
    canPinPost |= post.getLinkedClub() != null;
    if (canPinPost) {
      post.setPinned(pinned);
      postRepository.save(post);
      return;
    }

    throw new AuthException("you are not allowed to pin this post");
  }

  public Set<AuthorView> getAuthorizedPublish(TokenPayload auth) {
    Student student = studentService.getStudent(auth.getId());

    Set<AuthorView> authorStatus = new HashSet<>();
    authorStatus.add(authorFactory.entitytoView(student));

    if(auth.getRoles().contains(Roles.ADMIN)){
      authorStatus.add(authorFactory.admintoView());
      authorStatus.addAll(
        clubService.getAll()
          .stream()
          .map(c -> authorFactory.entitytoView(c))
          .collect(Collectors.toSet())
      );
    }else {
      authorStatus.addAll(
        studentService.getPublisherClubs(student)
          .stream()
          .map(c -> authorFactory.entitytoView(c))
          .collect(Collectors.toSet())
      );
    }

    return authorStatus;
  }

  public PostView getPostView(Long id) {
    Post post = getPost(id);
    if (post.getPrivate() && authService.isUserAnonymous()) {
      return null;
    }
    return postFactory.entityToView(post);
  }

  public Page<PostView> getPostsAuthor(Long id, boolean isPublic, int page) {
    if (isPublic) {
      Page<Post> posts = postRepository.findByAuthorIdAndIsPrivateOrderByCreationDateDesc(id, false, createPage(page));
      return posts.map(p -> postFactory.entityToView(p));
    }
    Page<Post> posts = postRepository.findByAuthorIdOrderByCreationDateDesc(id, createPage(page));
    return posts.map(p -> postFactory.entityToView(p));
  }

  public Post updatePost(Long id, PostUpdateDTO update, TokenPayload auth) {
    Post post = getPost(id);
    if (hasRightOnPost(auth, post)) {
      throw new AuthException("you cannot update this post");
    }
    post.setTitle(update.getTitle());
    post.setDescription(update.getContent());
    post.setPrivate(update.getPrivate());
    return postRepository.save(post);
  }

  private boolean hasRightOnPost(TokenPayload auth, Post post) {
    if (!auth.getRoles().contains(Roles.ADMIN)) {
      return !post.getAuthor().getId().equals(auth.getId()) && !auth.getClubsAdmin().contains(post.getAuthor().getId());
    }
    return false;
  }


  public List<PostView> getWaitingPosts(TokenPayload token) {
    List<Long> authors = new ArrayList<>();
    authors.add(token.getId());
    authors.addAll(token.getClubsPublisher());
    List<Post> waitingPosts = postRepository.findByPublishStateAndAuthor_IdInOrderByCreationDateDesc(PublishStateEnum.WAITING, authors);
    return waitingPosts.stream()
      .map(p -> postFactory.entityToView(p))
      .collect(Collectors.toList());
  }
}
