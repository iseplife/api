package com.iseplife.api.services;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.Roles;
import com.iseplife.api.constants.ThreadType;
import com.iseplife.api.dao.media.image.ImageRepository;
import com.iseplife.api.dao.post.*;
import com.iseplife.api.dto.CommentDTO;
import com.iseplife.api.dto.view.CommentView;
import com.iseplife.api.entity.Thread;
import com.iseplife.api.entity.ThreadInterface;
import com.iseplife.api.entity.post.Comment;
import com.iseplife.api.entity.post.Like;
import com.iseplife.api.exceptions.AuthException;
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
  AuthService authService;

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
  CommentFactory commentFactory;

  private Thread getThread(Long id) {
    Optional<Thread> thread = threadRepository.findById(id);
    if (thread.isEmpty())
      throw new IllegalArgumentException("Could not find a thread with id: " + id);

    return thread.get();
  }

  private Thread getThread(Object entityThread) {
    if (entityThread instanceof ThreadInterface)
      entityThread = ((ThreadInterface) entityThread).getThread();
    else if (entityThread instanceof Thread)
      return (Thread) entityThread;

    throw new IllegalArgumentException("Could not find a thread as this object doesn't seem to have one!");
  }

  public List<Like> getLikes(Long threadID) {
    Thread thread = getThread(threadID);
    return thread.getLikes();
  }

  public Boolean isLiked(Thread thread){
    return likeRepository.existsByThread_IdAndStudent_Id(thread.getId(), authService.getLoggedUser().getId());
  }

  public Boolean isLiked(Object entity) {
    Thread thread = getThread(entity);
    return isLiked(thread);
  }

  public void toggleLike(Long threadID, Long studentID) {
   Like like = likeRepository.findOneByThreadIdAndStudentId(threadID, studentID);

   //TODO: check permissions
    if (like != null) {
      likeRepository.delete(like);
    } else {
      like = new Like();
      like.setStudent(studentService.getStudent(studentID));
      like.setThread(getThread(threadID));
      likeRepository.save(like);
    }
  }

  public List<CommentView> getComments(Long threadID) {
    Thread thread = getThread(threadID);
    return thread.getComments()
      .stream()
      .map(c -> commentFactory.entityToView(c))
      .collect(Collectors.toList());
  }

  public Comment comment(Long threadID, CommentDTO dto, Long studentID) {
    Comment comment = new Comment();
    comment.setThread(getThread(threadID));
    comment.setMessage(dto.getMessage());
    comment.setStudent(studentService.getStudent(studentID));
    comment.setCreation(new Date());

    return commentRepository.save(comment);
  }

  public Comment editComment(Long comID, CommentDTO dto, Long studentID) {
    Optional<Comment> optional = commentRepository.findById(comID);

    if (optional.isEmpty())
      throw new IllegalArgumentException("could not find this comment");

    Comment comment = optional.get();
    if (!comment.getStudent().getId().equals(studentID)) {
      throw new AuthException("you cannot edit this comment");
    }

    comment.setMessage(dto.getMessage());
    comment.setLastEdition(new Date());
    return commentRepository.save(comment);
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
