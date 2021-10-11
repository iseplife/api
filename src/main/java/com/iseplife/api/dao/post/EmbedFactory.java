package com.iseplife.api.dao.post;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.dao.gallery.GalleryFactory;
import com.iseplife.api.dao.media.MediaFactory;
import com.iseplife.api.dao.poll.PollFactory;
import com.iseplife.api.dto.embed.view.EmbedView;
import com.iseplife.api.entity.post.embed.Embedable;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.entity.post.embed.media.Media;
import com.iseplife.api.entity.post.embed.poll.Poll;
import com.iseplife.api.exceptions.HttpBadRequestException;

public class EmbedFactory {

  public static EmbedView toView(Embedable embed) {
    if (embed == null || embed.getEmbedType() == null)
      return null;

    EmbedView view;
    switch (embed.getEmbedType()) {
      case EmbedType.GALLERY:
        view = ((Gallery) embed).getPseudo() ?
          GalleryFactory.toPseudoView((Gallery) embed) :
          GalleryFactory.toPreview((Gallery) embed);
        break;
      case EmbedType.POLL:
        view = PollFactory.toView((Poll) embed);
        break;
      case EmbedType.VIDEO:
      case EmbedType.IMAGE:
      case EmbedType.DOCUMENT:
        view = MediaFactory.toView((Media) embed);
        break;
      default:
        throw new HttpBadRequestException("invalid_attachment");
    }
    return view;

  }
}
