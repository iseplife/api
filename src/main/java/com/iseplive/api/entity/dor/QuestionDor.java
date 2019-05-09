package com.iseplive.api.entity.dor;

import javax.persistence.*;

/**
 * Created by Guillaume on 28/07/2017.
 * back
 */
@Entity
public class QuestionDor {
  @Id
  @GeneratedValue
  private Long id;

  @Column(unique = true)
  private int position;

  private String title;

  private boolean enableClub;
  private boolean enableStudent;
  private boolean enableEmployee;
  private boolean enableEvent;
  private boolean enableParty;

  private boolean enablePromo;
  private int promo;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public int getPromo() {
    return promo;
  }

  public void setPromo(int promo) {
    this.promo = promo;
  }

  public boolean isEnablePromo() {
    return enablePromo;
  }

  public void setEnablePromo(boolean enablePromo) {
    this.enablePromo = enablePromo;
  }
}
