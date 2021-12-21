package com.iseplife.api.dao.survey;

import com.iseplife.api.dto.survey.SurveyDTO;
import com.iseplife.api.entity.survey.Survey;
import org.springframework.stereotype.Component;

@Component
public class SurveyFactory {

  public Survey dtoToEntity(SurveyDTO dto) {
    Survey survey = new Survey();
    survey.setEnabled(dto.getEnabled());
    survey.setClosesAt(dto.getClosesAt());
    survey.setAnonymous(dto.getAnonymous());
    survey.setTitle(dto.getTitle());
    survey.setMultiple(dto.getMultiple());
    return survey;
  }

}
