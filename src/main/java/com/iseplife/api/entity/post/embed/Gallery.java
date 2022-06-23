package com.iseplife.api.entity.post.embed;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.post.embed.media.Image;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Getter @Setter @NoArgsConstructor
public class Gallery implements Embedable {
  @Id
  @GeneratedValue
  private Long id;

  private String name;

  private String description;

  private boolean pseudo = false;

  private Date creation;

  @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "gallery")
  private List<Image> images = new ArrayList<>();

  @ManyToOne
  private Feed feed;

  @ManyToOne
  private Club club;

  @PostLoad
  public void onCreate(){
    images.sort((a, b) -> (int)(a.getId() - b.getId()));
  }

  public List<Image> getPreview() {
    return images.subList(0, Math.min(images.size(), 5));
  }

  public String getEmbedType(){
    return EmbedType.GALLERY;
  }
}
