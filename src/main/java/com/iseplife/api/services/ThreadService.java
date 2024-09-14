package com.iseplife.api.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.constants.ThreadType;
import com.iseplife.api.dao.feed.FeedRepository;
import com.iseplife.api.dao.post.CommentRepository;
import com.iseplife.api.dao.post.LikeRepository;
import com.iseplife.api.dao.post.PostRepository;
import com.iseplife.api.dao.post.ReportRepository;
import com.iseplife.api.dao.post.projection.CommentProjection;
import com.iseplife.api.dao.post.projection.ReportProjection;
import com.iseplife.api.dao.subscription.NotificationRepository;
import com.iseplife.api.dao.subscription.SubscriptionRepository;
import com.iseplife.api.dao.thread.ThreadRepository;
import com.iseplife.api.dao.thread.projection.LikeProjection;
import com.iseplife.api.dto.thread.CommentDTO;
import com.iseplife.api.dto.thread.CommentEditDTO;
import com.iseplife.api.dto.thread.view.ThreadProjection;
import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.ThreadInterface;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.post.Comment;
import com.iseplife.api.entity.post.Like;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.post.Report;
import com.iseplife.api.entity.subscription.Notification;
import com.iseplife.api.entity.subscription.Subscribable;
import com.iseplife.api.entity.subscription.Subscription;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.exceptions.CommentMaxDepthException;
import com.iseplife.api.exceptions.http.HttpBadRequestException;
import com.iseplife.api.exceptions.http.HttpForbiddenException;
import com.iseplife.api.exceptions.http.HttpNotFoundException;
import com.iseplife.api.websocket.services.WSPostService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThreadService {
  @Lazy final private StudentService studentService;
  @Lazy final private ClubService clubService;
  final private ThreadRepository threadRepository;
  final private PostRepository postRepository;
  final private CommentRepository commentRepository;
  final private LikeRepository likeRepository;
  final private FeedRepository feedRepository;
  final private WSPostService postService;
  final private ReportRepository reportRepository;
  final private NotificationRepository notificationRepository;
  private final FirebaseMessengerService webPushService;
  final private SubscriptionRepository subscriptionRepository;

  public final static int MAX_COMMENT_LENGTH = 2000;

  public Thread getThread(Long id) {
    Optional<Thread> thread = threadRepository.findById(id);
    if (thread.isEmpty() || !SecurityService.hasReadAccess(getFeed(thread.get()).get()))
      throw new HttpNotFoundException("thread_not_found");

    return thread.get();
  }
  public Optional<Feed> getFeed(Thread thread) {
    switch(thread.getType()) {
      case POST:
        return feedRepository.findByPostThread(thread);
      case MEDIA:
        return feedRepository.findByMediaThread(thread);
      case COMMENT:
        return getFeed(commentRepository.findByThread(thread).get().getParentThread());
    }
    return null;
  }

  private Thread getThread(Object entityThread) {
    if (entityThread instanceof ThreadInterface)
      return ((ThreadInterface) entityThread).getThread();
    else if (entityThread instanceof Thread)
      return (Thread) entityThread;

    throw new RuntimeException("Could not find a thread as this object doesn't seem to have one!");
  }

  public ThreadProjection getView(Long id, Long student){
    return threadRepository.findThreadById(id, student);
  }

  public List<LikeProjection> getLikes(Long threadID) {
    return threadRepository.findLikesByThreadId(threadID);
  }

  public Boolean isLiked(Thread thread) {
    return isLiked(thread.getId());
  }

  public Boolean isLiked(Long thread) {
    return likeRepository.existsByThread_IdAndStudent_Id(thread, SecurityService.getLoggedId());
  }

  public Boolean isLiked(Object entity) {
    return isLiked(getThread(entity));
  }

  Long lastLikeNotif = 0L;

  public Boolean toggleLike(Long threadID, Long studentID) {
    Like like = likeRepository.findOneByThreadIdAndStudentId(threadID, studentID);

    boolean liked = like != null;

    //TODO: check permissions
    try {
      if (liked) {
        likeRepository.delete(like);

        return false;
      } else {
        like = new Like();
        like.setStudent(studentService.getStudent(studentID));
        like.setThread(getThread(threadID));
        likeRepository.save(like);

        if(System.currentTimeMillis() - lastLikeNotif > 1500) {
          lastLikeNotif = System.currentTimeMillis();
          checkPostNotif(like.getThread(), studentID);
        }

        return true;
      }
    } finally {
      postService.broadcastLikeChange(threadID, likeRepository.countByThreadId(threadID));
      postService.sendLikeUpdate(studentID, !liked, threadID);
    }
  }

  private void checkPostNotif(Thread thread, Long student) {
    Post post = postRepository.findPostByThread(thread);
    if(post == null) {
      System.out.println("liked not post");
    }
    if(post != null && !post.getNotified() &&
        thread.getLikes().size() > 10 &&
        (post.getFeed().getLastNotification() == null || System.currentTimeMillis() - post.getFeed().getLastNotification().getTime() > 1000 * 60 * 60) && // 1 par heure
        System.currentTimeMillis() - post.getPublicationDate().getTime() < 1000 * 60 * 60 * 3 / 2
      ) {
      Optional<Notification> optNotif = notificationRepository.findByIdInLink(post.getId());
      System.out.println("opt notif "+optNotif.isEmpty());
      if(optNotif.isEmpty())
        return;

      feedRepository.updateLastNotification(post.getFeed().getId());
      postRepository.setNotified(post.getId());

      Notification notif = optNotif.get();

      Subscribable subscribable = null;
      Feed feed = post.getFeed();
      if (feed.getGroup() != null)
        subscribable = feed.getGroup();
      else if (feed.getEvent() != null)
        subscribable = feed.getEvent();
      else if (feed.getClub() != null)
        subscribable = feed.getClub();
      else if (feed.getStudent() != null)
        subscribable = feed.getStudent();

      List<Subscription> subs = subscriptionRepository.findBySubscribed(subscribable);

      subs =
        subs.stream()
          .filter(Subscription::isExtensive)
          .collect(Collectors.toList());
      subs =
          subs.stream()
            .filter(sub -> (sub.getListener().getLastConnection() == null || System.currentTimeMillis() - sub.getListener().getLastConnection().getTime() > 1000 * 60 * 15) && sub.getListener().getId() != student && (!thread.getLikes().stream().anyMatch(like -> like.getStudent().getId() == sub.getId())))
            .collect(Collectors.toList());

      System.out.println("Sending notification based on like for "+post.getId()+" in a "+subscribable);
      webPushService.sendNotificationToAll(subs.stream().map(sub -> sub.getListener()).collect(Collectors.toList()), notif);
    }
  }
  public List<CommentProjection> getComments(Long threadID) {
    return commentRepository.findThreadComments(threadID, SecurityService.getLoggedId());
  }

  public CommentProjection getTrendingComment(Long thread) {
    List<CommentProjection> comment = commentRepository.findTrendingComments(
      thread,
      SecurityService.getLoggedId(),
      PageRequest.of(0, 1)
    );

    return comment.isEmpty() ? null : comment.get(0);
  }

  private Boolean canCommentOnThread(Thread thread) {
    return !threadRepository.doesParentCommentExist(thread);
  }

  public void reportComment(Long commentId, Long loggedId) {
    Student student = studentService.getStudent(loggedId);
    Comment comment = commentRepository.findById(commentId).get();

    if(!reportRepository.existsByCommentAndStudent(comment, student)) {
      Report report = new Report();
      report.setStudent(student);
      report.setComment(comment);
      reportRepository.save(report);
    }
  }

  public Comment comment(Long threadID, CommentDTO dto, Long studentID) {
    Thread thread = getThread(threadID);

    if (!canCommentOnThread(thread))
      throw new CommentMaxDepthException("Comment max depth reached (1)");

    if(dto.getMessage().length() > MAX_COMMENT_LENGTH)
      throw new HttpBadRequestException("comment_too_long");

    Comment comment = new Comment();
    comment.setParentThread(thread);
    comment.setThread(new Thread(ThreadType.COMMENT));
    comment.setMessage(dto.getMessage());
    comment.setStudent(studentService.getStudent(studentID));

    if (dto.getAsClub() != null) {
      Club club = clubService.getClub(dto.getAsClub());
      if (!SecurityService.hasAuthorAccessOn(club.getId()))
        throw new HttpForbiddenException("insufficient_rights");

      comment.setAsClub(club);
    }

    try {
      return commentRepository.save(comment);
    } finally {
      postService.broadcastCommentsUpdate(thread.getId(), commentRepository.countByParentThreadId(thread.getId()));
    }
  }

  public Comment editComment(Long id, Long comID, CommentEditDTO dto) {
    Optional<Comment> optional = commentRepository.findById(comID);

    if (optional.isEmpty())
      throw new HttpNotFoundException("comment_not_found");

    Comment comment = optional.get();
    if (!SecurityService.hasRightOn(comment, this))
      throw new HttpForbiddenException("insufficient_rights");

    if(dto.getMessage().length() > MAX_COMMENT_LENGTH)
      throw new HttpBadRequestException("comment_too_long");
    comment.setMessage(dto.getMessage());
    comment.setLastEdition(new Date());

    return commentRepository.save(comment);
  }

  public void deleteComment(Long comID, TokenPayload auth) {
    Optional<Comment> optional = commentRepository.findById(comID);

    if (optional.isEmpty())
      throw new HttpNotFoundException("comment_not_found");

    Comment comment = optional.get();
    if (!comment.getStudent().getId().equals(auth.getId()) && !auth.getRoles().contains(Roles.ADMIN))
      throw new HttpForbiddenException("insufficient_rights");

    commentRepository.deleteById(comID);

    postService.broadcastCommentsUpdate(comment.getParentThread().getId(), commentRepository.countByParentThreadId(comment.getParentThread().getId()));
  }

  public List<ReportProjection> getAllReports() {
    return reportRepository.getAllReports();
  }


}
