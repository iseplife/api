package com.iseplife.api.dao.rich;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.iseplife.api.dto.rich.view.RichLinkView;
import com.iseplife.api.entity.post.embed.rich.RichLink;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RichLinkFactory {
  final private ModelMapper mapper;

  public RichLinkView toView(RichLink richLink) {
    return mapper.map(richLink, RichLinkView.class);
  }
}
