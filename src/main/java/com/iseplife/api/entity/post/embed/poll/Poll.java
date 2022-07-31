package com.iseplife.api.entity.post.embed.poll;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.post.embed.Embedable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
public class Poll implements Embedable {
  @Id
  @GeneratedValue
  private Long id;

  private Date creation;
  private Date endsAt;
  private boolean multiple;
  private boolean anonymous;

  @OneToMany(mappedBy="poll", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<PollChoice> choices;

  @ManyToOne
  private Feed feed;

  public String getEmbedType(){
    return EmbedType.POLL;
  }
}
