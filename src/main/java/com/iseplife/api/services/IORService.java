package com.iseplife.api.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.iseplife.api.dao.isepdor.IORVotedSessionProjection;
import com.iseplife.api.dao.isepdor.IORSessionRepository;
import com.iseplife.api.dao.isepdor.IORVoteRepository;
import com.iseplife.api.dao.isepdor.IORQuestionProjection;
import com.iseplife.api.dao.isepdor.IORQuestionRepository;
import com.iseplife.api.dao.isepdor.IORSessionProjection;
import com.iseplife.api.entity.isepdor.IORQuestion;
import com.iseplife.api.entity.isepdor.IORSession;
import com.iseplife.api.entity.isepdor.IORVote;
import com.iseplife.api.entity.subscription.Subscribable;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.exceptions.http.HttpForbiddenException;
import com.iseplife.api.exceptions.http.HttpNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IORService {
  private final IORSessionRepository sessionRepository;
  private final IORQuestionRepository questionRepository;
  private final IORVoteRepository voteRepository;
  private final ClubService clubService;
  private final EventService eventService;
  private final StudentService studentService;
  
  public IORVotedSessionProjection getCurrentSessionWithVotes() {
    return sessionRepository.findOngoingSessionWithVotes(SecurityService.getLoggedId());
  }
  
  public IORSessionProjection getCurrentSession() {
    return sessionRepository.findOngoingSession();
  }
  
  public List<IORQuestionProjection> getQuestions(IORSession session){
    return questionRepository.findQuestions(SecurityService.getLoggedId(), session);
  }

  public IORSession getSession(Long sessionId) {
    Optional<IORSession> optSession = sessionRepository.findById(sessionId);
    if(optSession.isEmpty())
      throw new HttpNotFoundException("Session not found");
    
    IORSession session = optSession.get();
    
    if(session.getStart().after(new Date()) || session.getEnding().before(new Date()))
      throw new HttpForbiddenException("Session not available");
    
    return session;
  }

  public IORQuestionProjection getQuestion(Long questionId) {
    Optional<IORQuestionProjection> optQuestion = questionRepository.findQuestion(SecurityService.getLoggedId(), questionId);
    if(optQuestion.isEmpty())
      throw new HttpNotFoundException("Question not found");
    
    IORQuestionProjection question = optQuestion.get();
    IORSession session = question.getQuestion().getSession();
    
    if(session.getStart().after(new Date()) || session.getEnding().before(new Date()))
      throw new HttpForbiddenException("Session not available");
    
    return question;
  }

  public void updateVote(IORQuestion question, Long voted) {
    voteRepository.updateVote(studentService.getStudent(SecurityService.getLoggedId()), question, getVoted(question, voted));
  }
  
  public void createVote(IORQuestion question, Long voted) {
    Student student = studentService.getStudent(SecurityService.getLoggedId());
    IORVote vote = IORVote.builder()
        .question(question)
        .voter(student)
        .vote(getVoted(question, voted))
        .build();
    
    voteRepository.save(vote);
  }
  
  public Subscribable getVoted(IORQuestion question, Long voted) {
    switch(question.getType()) {
      case CLUB:
        return clubService.getClub(voted);
      case EVENT:
        return eventService.getEvent(voted);
      case STUDENT:
        return studentService.getStudent(voted);
      default:
        return null;
    }
  }

}
