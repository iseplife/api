package com.iseplife.api.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.constants.NotificationType;
import com.iseplife.api.constants.PostState;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.constants.ThreadType;
import com.iseplife.api.dao.post.AuthorFactory;
import com.iseplife.api.dao.post.PostRepository;
import com.iseplife.api.dao.post.projection.PostProjection;
import com.iseplife.api.dto.post.PostCreationDTO;
import com.iseplife.api.dto.post.PostUpdateDTO;
import com.iseplife.api.dto.view.AuthorView;
import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.post.embed.Embedable;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.entity.post.embed.media.Media;
import com.iseplife.api.entity.post.embed.poll.Poll;
import com.iseplife.api.entity.subscription.Notification;
import com.iseplife.api.entity.subscription.Subscribable;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.exceptions.http.HttpBadRequestException;
import com.iseplife.api.exceptions.http.HttpForbiddenException;
import com.iseplife.api.exceptions.http.HttpNotFoundException;
import com.iseplife.api.constants.PostState;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.media.MediaRepository;
import com.iseplife.api.dao.student.StudentRepository;
import com.iseplife.api.exceptions.AuthException;
import com.iseplife.api.exceptions.IllegalArgumentException;
import com.iseplife.api.websocket.PostMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.iseplife.api.websocket.services.WSPostService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
  @Lazy final private StudentService studentService;
  @Lazy final private ClubService clubService;
  @Lazy final private MediaService mediaService;
  @Lazy final private PollService pollService;
  @Lazy final private GalleryService galleryService;
  @Lazy final private FeedService feedService;
  @Lazy final private SecurityService securityService;
  @Lazy final private NotificationService notificationService;
  @Lazy final private WSPostService postsService;
  final private ModelMapper mapper;
  final private PostRepository postRepository;

  private final int POSTS_PER_PAGE = 8;

  private Post getPost(Long postID) {
    Optional<Post> post = postRepository.findById(postID);
    if (post.isEmpty())
      throw new HttpNotFoundException("poll_not_found");

    return post.get();
  }

  public Post getPostFromEmbed(Embedable embed) {
    Optional<Post> post = postRepository.findByEmbed(embed);
    if (post.isEmpty())
      throw new HttpNotFoundException("post_related_not_found");

    return post.get();
  }

  public Post createPost(PostCreationDTO dto) {
    Post post = mapper.map(dto, Post.class);
    Student author = securityService.getLoggedUser();

    // Author should be an admin or club publisher
    if (dto.getLinkedClub() != null && !SecurityService.hasAuthorAccessOn(dto.getLinkedClub()))
      throw new HttpForbiddenException("insufficient_rights");
    post.setLinkedClub(dto.getLinkedClub() != null ? clubService.getClub(dto.getLinkedClub()) : null);

    Feed feed;
    if(dto.getFeed() != null){
      feed = feedService.getFeed(dto.getFeed());
      // We disallow students to post on feeds they don't have write right on, and to post as a club in a student feed.
      if (!SecurityService.hasRightOn(feed) || (feed.getStudent() != null && dto.getLinkedClub() != null))
        throw new HttpForbiddenException("insufficient_rights");
    } else {
      feed = post.getLinkedClub() == null ?
        author.getFeed() :
        post.getLinkedClub().getFeed()
      ;
    }

    post.setFeed(feed);
    post.setAuthor(author);

    dto.getAttachements().forEach((type, id) -> bindAttachementToPost(type, id, post));

    post.setThread(new Thread(ThreadType.POST));
    post.setCreationDate(new Date());
    post.setState(dto.isDraft() ? PostState.DRAFT : PostState.READY);

    boolean customDate = post.getPublicationDate() != null && !dto.getPublicationDate().before(new Date());
    // If publication's date is missing or past, then set it at today to avoid any confusion
    post.setPublicationDate(customDate ?
      dto.getPublicationDate() :
      new Date()
    );


    Post postToReturn = postRepository.save(post);
    if(!dto.isDraft() && !customDate) {
      postsService.broadcastPost(postToReturn);

      Map<String, Object> map = new HashMap<>(Map.of(
          "post_id", post.getId(),
          "author_id", post.getAuthor().getId(),
          "author_name", (post.getLinkedClub() != null ? post.getLinkedClub() : securityService.getLoggedUser()).getName(),
          "content_text", post.getDescription(),
          "date", post.getPublicationDate()
      ));

      if(post.getLinkedClub() != null)
        map.put("club_id", post.getLinkedClub().getName());

      Notification.NotificationBuilder builder = Notification.builder()
          .icon(post.getLinkedClub() != null ? post.getLinkedClub().getLogoUrl() : securityService.getLoggedUser().getPicture())
          .informations(map);

      Subscribable subscribable = null;

      if (feed.getGroup() != null) {
        map.put("group_name", feed.getGroup().getName());
        builder.type(NotificationType.NEW_GROUP_POST)
               .link("post/group/"+feed.getGroup().getId()+"/"+post.getId());
        subscribable = feed.getGroup();
      } else if(feed.getClub() != null) {
        map.put("club_name", feed.getClub().getName());
        builder.type(NotificationType.NEW_CLUB_POST)
               .link("post/club/"+feed.getClub().getId()+"/"+post.getId());
        subscribable = feed.getClub();
      } else if(feed.getStudent() != null) {
        builder.type(NotificationType.NEW_STUDENT_POST)
               .link("post/student/"+feed.getStudent().getId()+"/"+post.getId());
        subscribable = feed.getStudent();
      }

      notificationService.delayNotification(
          builder,
          true,
          subscribable,
          () -> postRepository.existsById(postToReturn.getId())
      );
    }

    return postToReturn;
  }

  public void createPost(Gallery gallery) {
    Post post = new Post();
    post.setCreationDate(new Date());
    post.setPublicationDate(new Date());
    post.setState(PostState.READY);

    post.setEmbed(gallery);
    post.setFeed(gallery.getFeed());
    post.setDescription(gallery.getDescription());

    post.setAuthor(securityService.getLoggedUser());
    post.setLinkedClub(gallery.getClub());
    post.setThread(new Thread(ThreadType.POST));

    postRepository.save(post);
  }

  public Post updatePost(Long postID, PostUpdateDTO dto) {
    Post post = getPost(postID);
    if (!SecurityService.hasRightOn(post))
      throw new HttpForbiddenException("insufficient_rights");

    // Author should be an admin or club publisher
    if (dto.getLinkedClub() != null && !SecurityService.hasAuthorAccessOn(dto.getLinkedClub()))
      throw new HttpForbiddenException("insufficient_rights");

    post.setLinkedClub(dto.getLinkedClub() != null ? clubService.getClub(dto.getLinkedClub()) : null);

    post.setDescription(dto.getDescription());
    post.setPublicationDate(dto.getPublicationDate());

    if (!dto.getAttachements().isEmpty()) {
      removeEmbed(post.getEmbed());
      dto.getAttachements().forEach((type, id) -> bindAttachementToPost(type, id, post));
    }

    if (dto.getRemoveEmbed()) {
      removeEmbed(post.getEmbed());
      post.setEmbed(null);
    }

    Post postToReturn = postRepository.save(post);

    postsService.broadcastEdit(postToReturn);

    return postToReturn;
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
      throw new HttpForbiddenException("insufficient_rights");

    Embedable embed = post.getEmbed();
    if (embed != null)
      removeEmbed(embed);

    postRepository.deleteById(postID);

    postsService.broadcastRemove(postID);
  }

  public void updatePostPinnedStatus(Long postID, Boolean pinned) {
    Post post = getPost(postID);
    if (SecurityService.hasRightOn(post) && post.getLinkedClub() != null)
      throw new HttpForbiddenException("insufficient_rights");

    post.setPinned(pinned);
    postRepository.save(post);
  }

  public void updateForceHomepageStatus(Long postID, Boolean state) {
    Post post = getPost(postID);
    post.setForcedHomepage(state);

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
        throw new HttpBadRequestException("invalid_embed_type");
    }
    post.setEmbed(attachement);
  }

  @Cacheable(cacheNames = PostRepository.GET_AUTHORIZED_PUBLISH_CACHE, key = "'id_'.concat(#id).concat('_clubOnly_').concat(#clubOnly.booleanValue())")
  public Set<AuthorView> getAuthorizedPublish(Long id, Boolean clubOnly) {
    Student student = studentService.getStudent(id);
    Set<AuthorView> authorStatus = new HashSet<>();

    if (student.getRoles().contains(Roles.ADMIN)) {
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

  public Page<PostProjection> getMainFeedPost(Long loggedUser, int page){
    return postRepository.findHomepagePosts(
      loggedUser,
      PageRequest.of(page, POSTS_PER_PAGE,  Sort.by(Sort.Direction.DESC, "publicationDate"))
    );
  }
  public Page<PostProjection> getPreviousMainFeedPost(Long loggedUser, Date lastDate){
    return postRepository.findPreviousHomepagePosts(
      loggedUser,
      lastDate,
      PageRequest.of(0, POSTS_PER_PAGE,  Sort.by(Sort.Direction.DESC, "publicationDate"))
    );
  }

  public Page<PostProjection> getFeedPosts(Long feed, int page) {
    return postRepository.findCurrentFeedPost(
      feed,
      PostState.READY,
      SecurityService.getLoggedId(),
      SecurityService.hasRoles(Roles.ADMIN),
      PageRequest.of(page, POSTS_PER_PAGE,  Sort.by(Sort.Direction.DESC, "publicationDate"))
    );
  }
  public Page<PostProjection> getPreviousFeedPosts(Long feed, Date lastDate) {
    return postRepository.findPreviousCurrentFeedPost(
      feed,
      lastDate,
      PostState.READY,
      SecurityService.getLoggedId(),
      SecurityService.hasRoles(Roles.ADMIN),
      PageRequest.of(0, POSTS_PER_PAGE,  Sort.by(Sort.Direction.DESC, "publicationDate"))
    );
  }

  public List<PostProjection> getFeedPostsPinned(Feed feed) {
    return postRepository.findFeedPinnedPosts(
      feed,
      SecurityService.getLoggedId(),
      SecurityService.hasRoles(Roles.ADMIN)
    );
  }

  public PostProjection getFeedDrafts(Feed feed, Long author) {
    Optional<PostProjection> post = postRepository.findFeedDraft(feed, author);

    return post.orElse(null);
  }

  public Page<PostProjection> getAuthorPosts(Long id, int page, TokenPayload token) {
    return  postRepository.findAuthorPosts(id, SecurityService.getLoggedId(), token.getFeeds(), PageRequest.of(page, POSTS_PER_PAGE));
  }
}
