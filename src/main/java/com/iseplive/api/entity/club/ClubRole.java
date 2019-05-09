package com.iseplive.api.entity.club;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Guillaume on 30/07/2017.
 * back
 */
@Entity
public class ClubRole {
  @Id
  @GeneratedValue
  private Long id;

  private String name;

  @ManyToOne
  private Club club;

  @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ClubMember> members;

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

  @JsonIgnore
  public Club getClub() {
    return club;
  }

  public void setClub(Club club) {
    this.club = club;
  }
}
