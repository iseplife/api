package com.iseplife.api.controllers;

import java.util.List;

import javax.annotation.security.RolesAllowed;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.isepdor.IORQuestionProjection;
import com.iseplife.api.entity.isepdor.IORSession;
import com.iseplife.api.services.IORService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ior")
@RequiredArgsConstructor
public class IORController {
  private final IORService service;
  
  @GetMapping("current")
  @RolesAllowed({Roles.STUDENT})
  public IORSession getCurrentSession() {
  	return service.getCurrentSession();
  }
  
  @GetMapping("{sessionId}/questions")
  @RolesAllowed({Roles.STUDENT})
  public List<IORQuestionProjection> getQuestions(@PathVariable Long sessionId) {
    IORSession session = service.getSession(sessionId);
    return service.getQuestions(session);
  }
  
  @PostMapping("question/{questionId}")
  @RolesAllowed({Roles.STUDENT})
  public IORQuestionProjection updateVote(Long questionId, Long voted) {
    IORQuestionProjection question = service.getQuestion(questionId);
    
    if(question.getVote() != null) {
      System.out.println("Updating vote "+question.getVote()+" from "+question.getVote().getVote().getId()+" to "+voted);
      service.updateVote(question.getVote(), voted);
    } else
      service.createVote(question.getQuestion(), voted);
    
    return service.getQuestion(questionId);
  }
}
