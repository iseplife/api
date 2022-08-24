package com.iseplife.api.entity.post.embed.rich;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.entity.post.embed.Embedable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class RichLink implements Embedable {
  @Id
  @GeneratedValue
  private Long id;

  private String link;
  private String title;
  private String description;
  private String imageUrl;

  public String getEmbedType(){
    return EmbedType.RICH_LINK;
  }
}
