package com.iseplive.api.services;

import com.iseplive.api.conf.jwt.TokenPayload;
import com.iseplive.api.constants.PublishStateEnum;
import com.iseplive.api.constants.Roles;
import com.iseplive.api.dao.media.MediaRepository;
import com.iseplive.api.dao.post.*;
import com.iseplive.api.dao.student.StudentRepository;
import com.iseplive.api.dto.CommentDTO;
import com.iseplive.api.dto.PostDTO;
import com.iseplive.api.dto.PostUpdateDTO;
import com.iseplive.api.dto.view.CommentView;
import com.iseplive.api.dto.view.PostView;
import com.iseplive.api.entity.post.Comment;
import com.iseplive.api.entity.Event;
import com.iseplive.api.entity.post.Like;
import com.iseplive.api.entity.post.Post;
import com.iseplive.api.entity.club.Club;
import com.iseplive.api.entity.media.*;
import com.iseplive.api.entity.user.Student;
import com.iseplive.api.exceptions.AuthException;
import com.iseplive.api.exceptions.IllegalArgumentException;
import com.iseplive.api.utils.MediaUtils;
import com.iseplive.api.websocket.PostMessageService;
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
  PostFactory postFactory;

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


    // if creator is not an ADMIN or POST_MANAGER
    if (!auth.getRoles().contains(Roles.ADMIN) && !auth.getRoles().contains(Roles.POST_MANAGER)) {

      // Check if user is able to post in club's name
      if (post.getLinkedClub() != null && !auth.getClubsPublisher().contains(postDTO.getLinkedClubId())) {
        throw new AuthException("not allowed to create this post");
      }
    }

    post.setCreationDate(new Date());
    post.setPublishState(PublishStateEnum.PUBLISHED);

    post = postRepository.save(post);
    postMessageService.broadcastPost(auth.getId(), post);
    return post;
  }

  public List<CommentView> getComments(Long postId) {
    Post post = getPost(postId);
    return post.getComments()
      .stream()
      .map(c -> commentFactory.entityToView(c))
      .collect(Collectors.toList());
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

    if (post.getMedia() instanceof Event) {
      Event event = (Event) post.getMedia();
      mediaUtils.removeIfExistPublic(event.getImageUrl());
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

  public Comment commentPost(Long postId, CommentDTO dto, Long studentId) {
    Comment comment = new Comment();
    Post post = postRepository.findOne(postId);
    comment.setPost(post);
    comment.setMessage(dto.getMessage());
    Student student = studentService.getStudent(studentId);
    comment.setStudent(student);
    comment.setCreation(new Date());
    return commentRepository.save(comment);
  }

  public void setPublishState(Long id, PublishStateEnum state) {
    Post post = postRepository.findOne(id);
    post.setPublishState(state);
    postRepository.save(post);
  }

  public void togglePostLike(Long postId, Long id) {
    Like like = likeRepository.findOneByPostIdAndStudentId(postId, id);
    if(like != null){
      likeRepository.delete(like);
    } else {
      like = new Like();
      like.setStudent(studentService.getStudent(id));
      like.setPost(postRepository.findOne(postId));
      likeRepository.save(like);
    }
  }

  public void toggleCommentLike(Long comId, Long id) {
    Like like = likeRepository.findOneByCommentIdAndStudentId(comId, id);
    if(like != null){
      likeRepository.delete(like);
    } else {
      like = new Like();
      like.setStudent(studentService.getStudent(id));
      like.setComment(commentRepository.findOne(comId));
      likeRepository.save(like);
    }
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
    canPinPost |= auth.getRoles().contains(Roles.POST_MANAGER);
    canPinPost |= post.getLinkedClub() != null;
    if (canPinPost) {
      post.setPinned(pinned);
      postRepository.save(post);
      return;
    }

    throw new AuthException("you are not allowed to pin this post");
  }

  public List<Object> getAuthors(TokenPayload auth) {
    Student student = studentService.getStudent(auth.getId());
    List<Object> authors = new ArrayList<>();
    authors.add(student);


    if (auth.getRoles().contains(Roles.ADMIN)) {
      authors.addAll(clubService.getAll());
    } else {
      authors.addAll(clubService.getClubAuthors(student));
    }
    return authors;
  }

  public Boolean isPostLiked(Post post) {
    if (authService.isUserAnonymous()) return false;

    return likeRepository.findOneByPostIdAndStudentId(post.getId(), authService.getLoggedUser().getId()) != null;
  }

  public Boolean isCommentLiked(Comment comment) {
    if (authService.isUserAnonymous()) return false;

    return likeRepository.findOneByCommentIdAndStudentId(comment.getId(), authService.getLoggedUser().getId()) != null;
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

  public List<Like> getLikesPost(Long id) {
    Post post = getPost(id);
    return post.getLikes();
  }

  public List<Like> getLikesComment(Long id) {
    Comment comment = commentRepository.findOne(id);
    if (comment == null) {
      throw new IllegalArgumentException("could not find a comment with this id");
    }
    return comment.getLikes();
  }

  private boolean hasRightOnPost(TokenPayload auth, Post post) {
    if (!auth.getRoles().contains(Roles.ADMIN) && !auth.getRoles().contains(Roles.POST_MANAGER)) {
      return !post.getAuthor().getId().equals(auth.getId()) && !auth.getClubsAdmin().contains(post.getAuthor().getId());
    }
    return false;
  }

  public void deleteComment(Long comId, Long id) {
    Comment comment = commentRepository.findOne(comId);
    if (comment == null) {
      throw new IllegalArgumentException("could not find this comment");
    }
    if (!comment.getStudent().getId().equals(id)) {
      throw new AuthException("you cannot delete this comment");
    }
    commentRepository.delete(comment);
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
