package com.iseplife.api.entity.isepdor;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class IORSession {

  @Id
  @GeneratedValue
  private Long id;
  
  private Date start, end;
  
  @OneToOne(optional = true)
  private IORSession parentSession;

  @ManyToMany()
  private List<IORQuestion> questions;
}
