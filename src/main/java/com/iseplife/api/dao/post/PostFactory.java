package com.iseplife.api.dao.post;

import com.iseplife.api.dto.post.PostCreationDTO;
import com.iseplife.api.dto.post.view.PostView;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.services.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by Guillaume on 28/07/2017.
 * back
 */
@Component
public class PostFactory {

  @Autowired
  ThreadService threadService;

  @Autowired
  ModelMapper mapper;


  public Post dtoToEntity(PostCreationDTO post) {
    Post p = new Post();
    p.setDescription(post.getDescription());
    p.setPrivate(post.getPrivate());
    return p;
  }

  public PostView entityToView(PostProjection post) {
    PostView view = mapper.map(post, PostView.class);

    view.setEmbed(EmbedFactory.toView(post.getEmbed()));
    view.setLiked(threadService.isLiked(post.getThread()));
    view.setHasWriteAccess(SecurityService.hasRightOn(post));

    return view;
  }
}
