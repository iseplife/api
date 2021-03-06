package com.iseplife.api.dao.media;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.dto.media.view.DocumentView;
import com.iseplife.api.dto.media.view.ImageView;
import com.iseplife.api.dto.media.view.MediaView;
import com.iseplife.api.dto.media.view.VideoView;
import com.iseplife.api.entity.post.embed.media.Document;
import com.iseplife.api.entity.post.embed.media.Image;
import com.iseplife.api.entity.post.embed.media.Media;
import com.iseplife.api.entity.post.embed.media.Video;
import com.iseplife.api.exceptions.IllegalArgumentException;

public class MediaFactory {

  public static MediaView toView(Media media) {
    MediaView view;
    switch (media.getEmbedType()) {
      case EmbedType.VIDEO:
        Video video = (Video) media;
        view = new VideoView();
        ((VideoView) view).setTitle(video.getTitle());
        ((VideoView) view).setViews(video.getViews());
        break;
      case EmbedType.DOCUMENT:
        Document document = (Document) media;
        view = new DocumentView();
        ((DocumentView) view).setTitle(document.getTitle());

        break;
      case EmbedType.IMAGE:
        Image image = (Image) media;
        view = new ImageView();
        ((ImageView) view).setThread(image.getThread().getId());

      default:
        throw new IllegalArgumentException("Invalid attachments");
    }

    view.setId(media.getId());
    view.setCreation(media.getCreation());
    view.setNSFW(media.isNSFW());
    view.setName(media.getName());

    return view;
  }
}
