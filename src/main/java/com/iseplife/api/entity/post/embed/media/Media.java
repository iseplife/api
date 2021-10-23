package com.iseplife.api.entity.post.embed.media;

import com.iseplife.api.constants.MediaStatus;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.post.embed.Embedable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@DiscriminatorColumn(name = "mediaType")
@Getter @Setter @NoArgsConstructor
public abstract class Media implements Embedable {
  @Id
  @GeneratedValue
  private Long id;

  @Enumerated(EnumType.STRING)
  private MediaStatus status = MediaStatus.UNPROCESSED;

  /**
   * We can ignore this field in json as the Embeddable interface
   * will already give use the media type by giving us the embed type
   */
  @Column(insertable = false, updatable = false)
  private String mediaType;

  private boolean NSFW = false;
  private Date creation;
  private String name;


  @ManyToOne
  private Feed feed;
}
