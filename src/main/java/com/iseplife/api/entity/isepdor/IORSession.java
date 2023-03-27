package com.iseplife.api.entity.isepdor;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
  
  @OneToOne(optional = true, fetch = FetchType.LAZY)
  private IORSession parentSession;

  @OneToMany(mappedBy="session", fetch = FetchType.LAZY)
  private List<IORQuestion> questions;
}
