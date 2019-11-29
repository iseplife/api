package com.iseplife.api.entity.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iseplife.api.entity.post.Post;

import javax.persistence.*;
import java.util.Date;

@Entity
@DiscriminatorColumn(name = "embedType")
public abstract class Embed {
  @Id
  @GeneratedValue
  private Long id;

  @Column(insertable = false, updatable = false)
  private String embedType;

  @OneToOne(mappedBy = "embed", cascade = CascadeType.ALL)
  private Post post;

  private Date creation;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmbedType() {
    return embedType;
  }

  public Date getCreation() {
    return creation;
  }

  public void setCreation(Date creation) {
    this.creation = creation;
  }

  public Long getPostId() {
    return post != null ? post.getId() : 0;
  }

  @JsonIgnore
  public Post getPost() {
    return post;
  }

}
