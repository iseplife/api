package com.iseplife.api.dao.gallery;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.dao.club.ClubFactory;
import com.iseplife.api.dao.media.MediaFactory;
import com.iseplife.api.dto.gallery.view.GalleryPreview;
import com.iseplife.api.dto.gallery.view.GalleryView;
import com.iseplife.api.dto.gallery.view.PseudoGalleryView;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.entity.post.embed.media.Image;
import com.iseplife.api.services.SecurityService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class GalleryFactory {
  @Lazy final private ClubFactory clubFactory;
  @Lazy final private MediaFactory mediaFactory;
  final private ModelMapper mapper;
  
  @SuppressWarnings("unchecked")
  @PostConstruct
  public void init() {
    mapper.typeMap(Gallery.class, GalleryView.class)
      .addMappings(mapper -> {
        mapper
          .using(ctx -> ctx.getSource() != null ?
            clubFactory.toPreview((Club) ctx.getSource()) :
            null
          ).map(Gallery::getClub, GalleryView::setClub);
        mapper
          .using(ctx -> ((List<Image>) ctx.getSource()).stream().map(mediaFactory::toView).collect(Collectors.toList()))
          .map(Gallery::getImages, GalleryView::setImages);
      });
    
    mapper.typeMap(Gallery.class, PseudoGalleryView.class)
      .addMappings(mapper -> {
        mapper
          .using(ctx -> ((List<Image>) ctx.getSource()).stream().map(mediaFactory::toView).collect(Collectors.toList()))
          .map(Gallery::getImages, PseudoGalleryView::setImages);
      });
    mapper.typeMap(Gallery.class, GalleryPreview.class)
      .addMappings(mapper -> {
        mapper
          .using(ctx -> ((List<Image>) ctx.getSource()).stream().map(mediaFactory::toView).collect(Collectors.toList()))
          .map(Gallery::getPreview, GalleryPreview::setPreview);
      });
  }

  public GalleryView toView(Gallery gallery) {
    GalleryView view = mapper.map(gallery, GalleryView.class);
    view.setHasRight(SecurityService.hasRightOn(gallery));
    
    return view;
  }

  public PseudoGalleryView toPseudoView(Gallery gallery) {
    PseudoGalleryView view = mapper.map(gallery, PseudoGalleryView.class);
    view.setEmbedType(EmbedType.IMAGE);

    return view;
  }

  public GalleryPreview toPreview(Gallery gallery) {
    GalleryPreview preview = mapper.map(gallery, GalleryPreview.class);
    preview.setEmbedType(EmbedType.GALLERY);

    return preview;
  }

}
