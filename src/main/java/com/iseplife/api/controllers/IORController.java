package com.iseplife.api.controllers;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.isepdor.IORFactory;
import com.iseplife.api.dao.isepdor.IORQuestionProjection;
import com.iseplife.api.dao.isepdor.IORQuestionRepository;
import com.iseplife.api.dao.isepdor.IORSessionProjection;
import com.iseplife.api.dao.isepdor.IORSessionRepository;
import com.iseplife.api.dto.isepdor.view.IORSessionView;
import com.iseplife.api.dto.isepdor.view.IORVotedQuestionView;
import com.iseplife.api.entity.isepdor.IORQuestion;
import com.iseplife.api.entity.isepdor.IORQuestionType;
import com.iseplife.api.entity.isepdor.IORSession;
import com.iseplife.api.services.IORService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ior")
@RequiredArgsConstructor
public class IORController {
  private final IORService service;
  private final IORFactory factory;
  
  private final IORQuestionRepository questionRepository;
  private final IORSessionRepository sessionRepository;
  
  @GetMapping("")
  public void get() {
    if(true)
      return;
    IORSession session = IORSession.builder().start(new Date()).ending(new Date(new Date().getTime() + 6 * 24 * 60 * 60 * 1000)).build();
    String[] textQuestions = new String[] {
        "L’association de l’année",
        "L’association fantôme",
        "L’association bordel",
        "Le meilleur événement",
        "Le pire événement",
        "Le/La Président(e)",
        "Le meilleur membre du bureau",
        "le roi de l’isep",
        "la reine de l’isep",
        "L’épave",
        "Le clodo",
        "Le canard",
        "Le bg",
        "Le couple en or (1ère personne)",
        "Le couple en or (2ème personne)",
        "le/la tchatcheur(se)",
        "le/la beauf",
        "Le/la commère",
        "Le/la rageur(se)",
        "L’imbécile heureux(euse)",
        "Le melon",
        "L'influenceur(se)",
        "Le dj/musicien",
        "Le/la crack",
        "Le/la pro de la créa",
        "Le/la photographe",
    };
    
    session = sessionRepository.save(session);
    
    int i = 0;
    for(String question : textQuestions) {
      IORQuestion dbQuestion = IORQuestion.builder()
        .position(i++)
        .session(session)
        .title(question)
        .type(IORQuestionType.STUDENT)
        .build();
      dbQuestion = questionRepository.save(dbQuestion);
    }
    
    
  }
  
  @GetMapping("current")
  @RolesAllowed({Roles.STUDENT})
  public IORSessionView getCurrentSession() {
    IORSessionProjection session = service.getCurrentSession();
    if(session == null)
      return null;
  	return factory.toView(session);
  }
  
  @GetMapping("{sessionId}/questions")
  @RolesAllowed({Roles.STUDENT})
  public List<IORVotedQuestionView> getQuestions(@PathVariable Long sessionId) {
    IORSession session = service.getSession(sessionId);
    return service.getQuestions(session).stream().map(question -> {
      IORVotedQuestionView view = factory.toView(question);
      if(question.getQuestion().getParentQuestion() != null) {
        view.setChoices(
            questionRepository.findOptions(question.getQuestion().getParentQuestion().getId()).stream()
              .map(opt -> service.getVoted(question.getQuestion().getParentQuestion(), opt.getVoteId()))
              .map(entity -> factory.factorEntity(entity))
              .collect(Collectors.toList())
        );
        Collections.shuffle(view.getChoices());
      }
      return view;
    }).collect(Collectors.toList());
  }
  
  @PostMapping("question/{questionId}/{voted}")
  @RolesAllowed({Roles.STUDENT})
  public IORVotedQuestionView updateVote(@PathVariable Long questionId, @PathVariable Long voted) {
    IORQuestionProjection question = service.getQuestion(questionId);
    
    List<Long> entitiesId = question.getQuestion().getParentQuestion() == null ? null : questionRepository.findOptions(question.getQuestion().getParentQuestion().getId()).stream()
        .map(opt -> opt.getVoteId())
        .collect(Collectors.toList());
    
    if(question.getQuestion().getParentQuestion() != null && !entitiesId.contains(voted))
      throw new IllegalArgumentException(voted+" is not listed in the vote candidates.");
    
    if(question.getVote() != null) {
      System.out.println("Updating vote "+question.getQuestion().getId()+" from "+question.getVote().getId()+" to "+voted);
      service.updateVote(question.getQuestion(), voted);
    } else
      service.createVote(question.getQuestion(), voted);

    IORVotedQuestionView view = factory.toView(service.getQuestion(questionId));
    if(question.getQuestion().getParentQuestion() != null) {
      view.setChoices(
          entitiesId.stream()
          .map(id -> service.getVoted(question.getQuestion().getParentQuestion(), id))
          .map(entity -> factory.factorEntity(entity))
          .collect(Collectors.toList())
      );
      Collections.shuffle(view.getChoices());
    }
    return view;
  }
}
