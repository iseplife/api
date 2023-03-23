package com.iseplife.api.entity.isepdor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.MetaValue;

import com.iseplife.api.constants.SubscribableType;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.subscription.Subscribable;
import com.iseplife.api.entity.user.Student;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class IORVote {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private Student voter;
  
  @ManyToOne
  private IORQuestion question;
  
  @Any(
      fetch = FetchType.EAGER,
      metaColumn = @Column(name = "vote_type")
    )
    @AnyMetaDef(
      idType = "long",
      metaType = "string",
      metaValues = {
        @MetaValue(value = SubscribableType.STUDENT, targetEntity = Student.class),
        @MetaValue(value = SubscribableType.CLUB, targetEntity = Club.class),
        @MetaValue(value = SubscribableType.EVENT, targetEntity = Event.class),
      }
    )
    @JoinColumn(name = "vote_id")
    private Subscribable vote;
}
