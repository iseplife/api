package com.iseplive.api.dto.dor;

/**
 * Created by Guillaume on 10/02/2018.
 * back
 */
public class VoteDorDTO {
  private Long questionID;

  private Long authorID;
  private Long eventID;

  public Long getQuestionID() {
    return questionID;
  }

  public void setQuestionID(Long questionID) {
    this.questionID = questionID;
  }

  public Long getEventID() {
    return eventID;
  }

  public void setEventID(Long eventID) {
    this.eventID = eventID;
  }

  public Long getAuthorID() {
    return authorID;
  }

  public void setAuthorID(Long authorID) {
    this.authorID = authorID;
  }

}
