package com.iseplive.api.dto.dor;

import java.util.Date;

/**
 * Created by Guillaume on 11/02/2018.
 * back
 */
public class SessionDorDTO {
  private Date firstTurn;
  private Date secondTurn;
  private Date result;

  public Date getFirstTurn() {
    return firstTurn;
  }

  public void setFirstTurn(Date firstTurn) {
    this.firstTurn = firstTurn;
  }

  public Date getSecondTurn() {
    return secondTurn;
  }

  public void setSecondTurn(Date secondTurn) {
    this.secondTurn = secondTurn;
  }

  public Date getResult() {
    return result;
  }

  public void setResult(Date result) {
    this.result = result;
  }
}
