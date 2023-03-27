package com.iseplife.api.dao.isepdor;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.iseplife.api.dao.club.ClubFactory;
import com.iseplife.api.dao.event.EventFactory;
import com.iseplife.api.dao.student.StudentFactory;
import com.iseplife.api.dto.isepdor.view.IORVoteView;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.isepdor.IORVote;
import com.iseplife.api.entity.subscription.Subscribable;
import com.iseplife.api.entity.user.Student;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class IORFactory {
  final private ModelMapper mapper;
  final private StudentFactory studentFactory;
  final private EventFactory eventFactory;
  final private ClubFactory clubFactory;
  
  @PostConstruct()
  public void init() {
    mapper.typeMap(IORVote.class, IORVoteView.class)
      .addMappings(mapper -> {
        mapper
          .using(ctx -> factorEntity((Subscribable) ctx.getSource()))
          .map(IORVote::getVote, IORVoteView::setVote);
      });
  }
  
  private Object factorEntity(Subscribable entity) {
    if(entity instanceof Student)
      return studentFactory.toPreview((Student)entity);
    if(entity instanceof Event)
      return eventFactory.toPreview((Event)entity);
    if(entity instanceof Club)
      return clubFactory.toPreview((Club)entity);
    
    return null;
  }

  public IORVoteView toView(IORVote vote) {
    return mapper.map(vote, IORVoteView.class);
  }

}
