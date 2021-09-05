package com.iseplife.api.services;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.media.image.ImageRepository;
import com.iseplife.api.dao.post.*;
import com.iseplife.api.dto.thread.CommentDTO;
import com.iseplife.api.dto.thread.CommentEditDTO;
import com.iseplife.api.dto.view.CommentView;
import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.ThreadInterface;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.post.Comment;
import com.iseplife.api.entity.post.Like;
import com.iseplife.api.exceptions.AuthException;
import com.iseplife.api.exceptions.CommentMaxDepthException;
import com.iseplife.api.exceptions.IllegalArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ThreadService {

  @Autowired
  ThreadRepository threadRepository;

  @Autowired
  PostRepository postRepository;

  @Autowired
  CommentRepository commentRepository;

  @Autowired
  ImageRepository imageRepository;

  @Autowired
  LikeRepository likeRepository;

  @Autowired
  StudentService studentService;

  @Autowired
  SecurityService securityService;

  @Autowired
  ClubService clubService;

  @Autowired
  CommentFactory commentFactory;

  private Thread getThread(Long id) {
    Optional<Thread> thread = threadRepository.findById(id);
    if (thread.isEmpty() || !SecurityService.hasReadAccess(thread.get().getFeed()))
      throw new IllegalArgumentException("Could not find a thread with id: " + id);

    return thread.get();
  }

  private Thread getThread(Object entityThread) {
    if (entityThread instanceof ThreadInterface)
      return ((ThreadInterface) entityThread).getThread();
    else if (entityThread instanceof Thread)
      return (Thread) entityThread;

    throw new IllegalArgumentException("Could not find a thread as this object doesn't seem to have one!");
  }

  public List<Like> getLikes(Long threadID) {
    Thread thread = getThread(threadID);
    return thread.getLikes();
  }

  public Boolean isLiked(Thread thread) {
    return isLiked(thread.getId());
  }

  public Boolean isLiked(Long thread) {
    return likeRepository.existsByThread_IdAndStudent_Id(thread, securityService.getLoggedUser().getId());
  }

  public Boolean isLiked(Object entity) {
    return isLiked(getThread(entity));
  }

  public Boolean toggleLike(Long threadID, Long studentID) {
    Like like = likeRepository.findOneByThreadIdAndStudentId(threadID, studentID);

    //TODO: check permissions
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
  }

  public List<CommentView> getComments(Long threadID) {
    Thread thread = getThread(threadID);
    return thread.getComments()
      .stream()
      .map(c -> commentFactory.toView(c))
      .collect(Collectors.toList());
  }

  public CommentView getTrendingComment(Long thread){
    return commentFactory.toView(
      commentRepository.findTrendingComments(thread, SecurityService.getLoggedId())
    );
  }

  private Boolean canCommentOnThread(Thread thread){
    if(thread.getComment() != null){
      return thread.getComment().getParentThread().getComment() == null;
    }
    return true;
  }

  public CommentView comment(Long threadID, CommentDTO dto, Long studentID) {
    Thread thread = getThread(threadID);

    if (!canCommentOnThread(thread))
      throw new CommentMaxDepthException("Comment max depth reached (1)");

    Comment comment = new Comment();
    comment.setParentThread(thread);
    comment.setThread(new Thread());
    comment.setMessage(dto.getMessage());
    comment.setStudent(studentService.getStudent(studentID));

    if (dto.getAsClub() != null) {
      Club club = clubService.getClub(dto.getAsClub());

      if (!SecurityService.hasRightOn(club))
        throw new AuthException("You are not allowed to comment as this club (id:" + club.getId() + ")");

      comment.setAsClub(club);
    }

    return commentFactory.toView(commentRepository.save(comment));
  }

  public CommentView editComment(Long id, Long comID, CommentEditDTO dto) {
    Optional<Comment> optional = commentRepository.findById(comID);

    if (optional.isEmpty())
      throw new IllegalArgumentException("could not find this comment");

    Comment comment = optional.get();
    if (!SecurityService.hasRightOn(comment))
      throw new AuthException("you cannot edit this comment");

    comment.setMessage(dto.getMessage());
    comment.setLastEdition(new Date());

    return commentFactory.toView(commentRepository.save(comment));
  }

  public void deleteComment(Long comID, TokenPayload auth) {
    Optional<Comment> optional = commentRepository.findById(comID);

    if (optional.isEmpty())
      throw new IllegalArgumentException("could not find this comment");

    Comment comment = optional.get();
    if (!comment.getStudent().getId().equals(auth.getId()) && !auth.getRoles().contains(Roles.ADMIN)) {
      throw new AuthException("you cannot delete this comment");
    }
    commentRepository.deleteById(comID);
  }


}
