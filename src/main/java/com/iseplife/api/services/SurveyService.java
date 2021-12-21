package com.iseplife.api.services;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dao.feed.FeedRepository;
import com.iseplife.api.dao.survey.SurveyChoiceRepository;
import com.iseplife.api.dao.survey.SurveyFactory;
import com.iseplife.api.dao.survey.SurveyRepository;
import com.iseplife.api.dao.survey.SurveyVoteRepository;
import com.iseplife.api.dto.survey.SurveyDTO;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.survey.Survey;
import com.iseplife.api.entity.survey.SurveyChoice;
import com.iseplife.api.entity.survey.SurveyVote;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.exceptions.http.HttpNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SurveyService {

  @Autowired
  SurveyRepository surveyRepository;

  @Autowired
  SurveyChoiceRepository surveyChoiceRepository;

  @Autowired
  SurveyVoteRepository surveyVoteRepository;

  @Autowired
  SurveyFactory surveyFactory;

  @Autowired
  SecurityService securityService;

  @Autowired
  FeedRepository feedRepository;

  private Survey postSurvey(SurveyDTO dto) {
    Survey survey = surveyFactory.dtoToEntity(dto);
    survey.setTargets((Set<Feed>) feedRepository.findAllById(dto.getFeeds()));
    return surveyRepository.save(survey);
  }

  private Set<Survey> getCurrentSurveys(TokenPayload token){

    return surveyRepository.findCurrentSurveys(true, new Date(), token.getFeeds());
  }

  private SurveyVote vote(Long idChoice) {
    Student student = securityService.getLoggedUser();

    Optional<SurveyChoice> surveyChoiceOptional = surveyChoiceRepository.findById(idChoice);

    if (surveyChoiceOptional.isEmpty())
      throw new HttpNotFoundException("survey_choice_related_not_found");

    SurveyChoice surveyChoice = surveyChoiceOptional.get();
    Survey survey = surveyChoice.getSurvey();
    List<SurveyVote> surveyVotes = surveyVoteRepository.findAllByVoterAndSurvey(survey.getId(), student.getId());

    if (surveyVotes.size() >= 1 && !survey.getMultiple())
      throw new HttpNotFoundException("survey_allow_single_vote");

    for (SurveyVote surveyVote: surveyVotes) {
      if (surveyVote.getChoice().getId().equals(idChoice))
        throw new HttpNotFoundException("cannot_vote_for_the_same_choice");
    }

    SurveyVote surveyVote =  new SurveyVote();
    surveyVote.setVoter(student);
    surveyVote.setChoice(surveyChoice);

    return surveyVoteRepository.save(surveyVote);
  }
}
