package com.iseplive.api.entity;

import javax.persistence.*;

@Entity
public class Feed {

  @Id
  @GeneratedValue
  private Long id;

  @Column(unique = true)
  private String name;

  public Long getId() { return id; }

  public void setId(Long id) { this.id = id; }

  public String getName() { return name; }

  public void setName(String name) { this.name = name; }
}
