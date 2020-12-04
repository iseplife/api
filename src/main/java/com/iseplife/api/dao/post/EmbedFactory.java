package com.iseplife.api.dao.post;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.dao.gallery.GalleryFactory;
import com.iseplife.api.dao.poll.PollFactory;
import com.iseplife.api.dto.post.view.EmbedView;
import com.iseplife.api.entity.post.embed.Embedable;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.entity.post.embed.poll.Poll;
import com.iseplife.api.exceptions.IllegalArgumentException;

public class EmbedFactory {

  public static EmbedView toView(Embedable embed) {
    EmbedView view;
    switch (embed.getEmbedType()) {
      case EmbedType.GALLERY:
        view = GalleryFactory.toPreview((Gallery) embed);
        view.setEmbedType(EmbedType.GALLERY);
        return view;
      case EmbedType.POLL:
        view = PollFactory.toView((Poll) embed);
        view.setEmbedType(EmbedType.POLL);
        return view;
      case EmbedType.VIDEO:
      case EmbedType.DOCUMENT:
      case EmbedType.IMAGE:
        //return MediaFa.toView((Poll) embed);
      default:
        throw new IllegalArgumentException("Invalid attachments");
    }


  }
}
