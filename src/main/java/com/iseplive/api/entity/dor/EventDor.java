package com.iseplive.api.entity.dor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Guillaume on 09/02/2018.
 * back
 */
@Entity
public class EventDor {

  @Id
  @GeneratedValue
  private Long id;

  private String name;

  private boolean party;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isParty() {
    return party;
  }

  public void setParty(boolean party) {
    this.party = party;
  }
}
