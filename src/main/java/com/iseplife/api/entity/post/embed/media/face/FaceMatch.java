package com.iseplife.api.entity.post.embed.media.face;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.iseplife.api.entity.post.embed.media.Image;
import com.iseplife.api.entity.user.Face;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class FaceMatch {

  @Id
  @GeneratedValue
  private Long id;
  
  private int x, y, width, height;
  
  private float score, distance;

  
  @ManyToOne
  private Image image;
  @ManyToOne
  private Face face;
  
}
