package com.iseplive.api.entity.user;

import javax.persistence.*;

/**
 * Created by Guillaume on 03/08/2017.
 * back
 */
@Entity
@DiscriminatorColumn(name = "authorType")
public abstract class Author {
  @Id
  @GeneratedValue
  private Long id;

  private boolean archived = false;

  @Column(updatable = false, insertable = false)
  private String authorType;



  public String getAuthorType() {
    return authorType;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }
}
