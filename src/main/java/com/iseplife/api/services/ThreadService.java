package com.iseplife.api.services;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.constants.ThreadType;
import com.iseplife.api.dao.post.*;
import com.iseplife.api.dao.post.projection.CommentProjection;
import com.iseplife.api.dao.thread.ThreadRepository;
import com.iseplife.api.dto.thread.CommentDTO;
import com.iseplife.api.dto.thread.CommentEditDTO;
import com.iseplife.api.dto.thread.view.ThreadProjection;
import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.ThreadInterface;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.post.Comment;
import com.iseplife.api.entity.post.Like;
import com.iseplife.api.exceptions.http.HttpForbiddenException;
import com.iseplife.api.exceptions.CommentMaxDepthException;
import com.iseplife.api.exceptions.http.HttpNotFoundException;
import com.iseplife.api.websocket.services.WSPostService;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ThreadService {
  @Lazy final private StudentService studentService;
  @Lazy final private ClubService clubService;
  final private ThreadRepository threadRepository;
  final private CommentRepository commentRepository;
  final private LikeRepository likeRepository;
  final private WSPostService postService;

  public Thread getThread(Long id) {
    Optional<Thread> thread = threadRepository.findById(id);
    if (thread.isEmpty() || !SecurityService.hasReadAccess(thread.get().getFeed()))
      throw new HttpNotFoundException("thread_not_found");

    return thread.get();
  }

  private Thread getThread(Object entityThread) {
    if (entityThread instanceof ThreadInterface)
      return ((ThreadInterface) entityThread).getThread();
    else if (entityThread instanceof Thread)
      return (Thread) entityThread;

    throw new RuntimeException("Could not find a thread as this object doesn't seem to have one!");
  }

  public ThreadProjection getView(Long id, Long student){
    Thread thread = getThread(id);

    return threadRepository.findThreadById(thread.getId(), student);
  }

  public List<Like> getLikes(Long threadID) {
    Thread thread = getThread(threadID);
    return thread.getLikes();
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

  @CacheEvict(value = LikeRepository.EXISTS_BY_THREAD_AND_STUDENT_CACHE, key = "{#threadID, #studentID}")
  public Boolean toggleLike(Long threadID, Long studentID) {
    Like like = likeRepository.findOneByThreadIdAndStudentId(threadID, studentID);

    //TODO: check permissions
    try {
      if (like != null) {
        likeRepository.delete(like);
  
        return false;
      } else {
        like = new Like();
        like.setStudent(studentService.getStudent(studentID));
        like.setThread(getThread(threadID));
        likeRepository.save(like);
  
        return true;
      }
    } finally {
      postService.broadcastLikeChange(threadID, likeRepository.countByThreadId(threadID));
    }
  }

  public List<CommentProjection> getComments(Long threadID) {
    return commentRepository.findThreadComments(threadID);
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
    if (thread.getComment() != null) {
      return thread.getComment().getParentThread().getComment() == null;
    }
    return true;
  }

  public Comment comment(Long threadID, CommentDTO dto, Long studentID) {
    Thread thread = getThread(threadID);

    if (!canCommentOnThread(thread))
      throw new CommentMaxDepthException("Comment max depth reached (1)");

    Comment comment = new Comment();
    comment.setParentThread(thread);
    comment.setThread(new Thread(ThreadType.COMMENT));
    comment.setMessage(dto.getMessage());
    comment.setStudent(studentService.getStudent(studentID));

    if (dto.getAsClub() != null) {
      Club club = clubService.getClub(dto.getAsClub());
      if (!SecurityService.hasRightOn(club))
        throw new HttpForbiddenException("insufficient_rights");

      comment.setAsClub(club);
    }

    return commentRepository.save(comment);
  }

  public Comment editComment(Long id, Long comID, CommentEditDTO dto) {
    Optional<Comment> optional = commentRepository.findById(comID);

    if (optional.isEmpty())
      throw new HttpNotFoundException("comment_not_found");

    Comment comment = optional.get();
    if (!SecurityService.hasRightOn(comment))
      throw new HttpForbiddenException("insufficient_rights");

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
  }


}
