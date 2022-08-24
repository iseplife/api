package com.iseplife.api.dao.post;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.dao.gallery.GalleryFactory;
import com.iseplife.api.dao.media.MediaFactory;
import com.iseplife.api.dao.poll.PollFactory;
import com.iseplife.api.dao.rich.RichLinkFactory;
import com.iseplife.api.dto.view.EmbedView;
import com.iseplife.api.entity.post.embed.Embedable;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.entity.post.embed.media.Media;
import com.iseplife.api.entity.post.embed.poll.Poll;
import com.iseplife.api.entity.post.embed.rich.RichLink;
import com.iseplife.api.exceptions.http.HttpBadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmbedFactory {
  @Lazy final private GalleryFactory galleryFactory;
  @Lazy final private MediaFactory mediaFactory;
  @Lazy final private PollFactory pollFactory;
  @Lazy final private RichLinkFactory richLinkFactory;

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
        view = pollFactory.toView((Poll) embed);
        break;
      case EmbedType.RICH_LINK:
        view = richLinkFactory.toView((RichLink) embed);
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
