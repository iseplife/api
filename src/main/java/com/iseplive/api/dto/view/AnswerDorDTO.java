package com.iseplive.api.dto.view;

import com.iseplive.api.entity.dor.VoteDor;

public class AnswerDorDTO {
  private Long idAnswer;
  private AnswerDorType type;
  private VoteDor voteDor;
  private Long score = 0L;

  public AnswerDorDTO(Long idAnswer, AnswerDorType type, VoteDor voteDor) {
    this.idAnswer = idAnswer;
    this.type = type;
    this.voteDor = voteDor;
  }


  public String getName() {
    return String.format("%d_%s", idAnswer, type);
  }

  public Long getIdAnswer() {
    return idAnswer;
  }

  public void setIdAnswer(Long idAnswer) {
    this.idAnswer = idAnswer;
  }

  public AnswerDorType getType() {
    return type;
  }

  public void setType(AnswerDorType type) {
    this.type = type;
  }

  public Long getScore() {
    return score;
  }

  public void setScore(Long score) {
    this.score = score;
  }

  public VoteDor getVoteDor() {
    return voteDor;
  }

  public void setVoteDor(VoteDor voteDor) {
    this.voteDor = voteDor;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof AnswerDorDTO) {
      return ((AnswerDorDTO) obj).getIdAnswer().equals(idAnswer) && ((AnswerDorDTO) obj).getType().equals(type);
    }
    return super.equals(obj);
  }
}
