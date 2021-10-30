package com.iseplife.api.dao.post;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.dao.gallery.GalleryFactory;
import com.iseplife.api.dao.media.MediaFactory;
import com.iseplife.api.dao.poll.PollFactory;
import com.iseplife.api.dto.view.EmbedView;
import com.iseplife.api.entity.post.embed.Embedable;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.entity.post.embed.media.Media;
import com.iseplife.api.entity.post.embed.poll.Poll;
import com.iseplife.api.exceptions.http.HttpBadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmbedFactory {
  @Lazy final private GalleryFactory galleryFactory;
  @Lazy final private MediaFactory mediaFactory;

  public EmbedView toView(Embedable embed) {
    if (embed == null || embed.getEmbedType() == null)
      return null;

    EmbedView view;
    switch (embed.getEmbedType()) {
      case EmbedType.GALLERY:
        view = ((Gallery) embed).isPseudo() ?
          galleryFactory.toPseudoView((Gallery) embed) :
          galleryFactory.toPreview((Gallery) embed);
        break;
      case EmbedType.POLL:
        view = PollFactory.toView((Poll) embed);
        break;
      case EmbedType.VIDEO:
      case EmbedType.IMAGE:
      case EmbedType.DOCUMENT:
        view = mediaFactory.toView((Media) embed);
        break;
      default:
        throw new HttpBadRequestException("invalid_attachment");
    }
    return view;

  }
}
