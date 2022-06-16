package com.iseplife.api.dao.media;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.dto.media.view.*;
import com.iseplife.api.entity.post.embed.media.Document;
import com.iseplife.api.entity.post.embed.media.Image;
import com.iseplife.api.entity.post.embed.media.Media;
import com.iseplife.api.entity.post.embed.media.Video;
import com.iseplife.api.exceptions.http.HttpBadRequestException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MediaFactory {
  final private ModelMapper mapper;

  public MediaView toBasicView(Media media) {
    return mapper.map(media, MediaView.class);
  }

  public static MediaNameView toNameView(String name){
    MediaNameView v = new MediaNameView();
    v.setName(name);

    return v;
  }

  public static MediaNameView toNameView(Media media){
    MediaNameView v = new MediaNameView();
    v.setName(media.getName());

    return v;
  }

  public MediaView toView(Media media) {
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
        ((DocumentView) view).setSizeBytes(document.getSizeBytes());
        break;
      case EmbedType.IMAGE:
        Image image = (Image) media;
        view = new ImageView();
        if(image.getThread() != null)
          ((ImageView) view).setThread(image.getThread().getId());
        ((ImageView) view).setColor(image.getColor());
        ((ImageView) view).setRatio(image.getRatio());
        break;
      default:
        throw new HttpBadRequestException("invalid_attachment");
    }

    view.setId(media.getId());
    view.setCreation(media.getCreation());
    view.setNSFW(media.isNSFW());
    view.setName(media.getName());
    view.setEmbedType(media.getEmbedType());
    view.setStatus(media.getStatus());
    return view;
  }
}
