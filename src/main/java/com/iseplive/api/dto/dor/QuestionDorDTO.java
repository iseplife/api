package com.iseplive.api.dto.dor;

/**
 * Created by Guillaume on 11/02/2018.
 * back
 */
public class QuestionDorDTO {
  private int position;
  private String title;

  private boolean enableClub;
  private boolean enableStudent;
  private boolean enableEmployee;
  private boolean enableEvent;
  private boolean enableParty;

  private Integer promo;

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public boolean isEnableClub() {
    return enableClub;
  }

  public void setEnableClub(boolean enableClub) {
    this.enableClub = enableClub;
  }

  public boolean isEnableStudent() {
    return enableStudent;
  }

  public void setEnableStudent(boolean enableStudent) {
    this.enableStudent = enableStudent;
  }

  public boolean isEnableEmployee() {
    return enableEmployee;
  }

  public void setEnableEmployee(boolean enableEmployee) {
    this.enableEmployee = enableEmployee;
  }

  public boolean isEnableEvent() {
    return enableEvent;
  }

  public void setEnableEvent(boolean enableEvent) {
    this.enableEvent = enableEvent;
  }

  public boolean isEnableParty() {
    return enableParty;
  }

  public void setEnableParty(boolean enableParty) {
    this.enableParty = enableParty;
  }

  public Integer getPromo() {
    return promo;
  }

  public void setPromo(Integer promo) {
    this.promo = promo;
  }
}
