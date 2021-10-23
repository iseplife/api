package com.iseplife.api.dao.gallery;

import com.iseplife.api.constants.EmbedType;
import com.iseplife.api.dao.club.ClubFactory;
import com.iseplife.api.dao.media.MediaFactory;
import com.iseplife.api.dto.gallery.view.GalleryPreview;
import com.iseplife.api.dto.gallery.view.GalleryView;
import com.iseplife.api.dto.gallery.view.PseudoGalleryView;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.services.SecurityService;

import java.util.stream.Collectors;

public class GalleryFactory {

  static public GalleryView toView(Gallery gallery){
    GalleryView view = new GalleryView();
    view.setId(gallery.getId());
    view.setName(gallery.getName());
    view.setCreation(gallery.getCreation());
    view.setClub(gallery.getClub() == null ? null : ClubFactory.toPreview(gallery.getClub()));
    view.setImages(
      gallery.getImages().stream().map(MediaFactory::toView).collect(Collectors.toList())
    );
    view.setHasRight(SecurityService.hasRightOn(gallery));

    return view;
  }

  static public PseudoGalleryView toPseudoView(Gallery gallery){
    PseudoGalleryView view = new PseudoGalleryView();
    view.setId(gallery.getId());
    view.setEmbedType(EmbedType.IMAGE);
    view.setImages(
      gallery.getImages().stream().map(MediaFactory::toView).collect(Collectors.toList())
    );
    return view;
  }

  static public GalleryPreview toPreview(Gallery gallery){
    GalleryPreview preview = new GalleryPreview();
    preview.setId(gallery.getId());
    preview.setName(gallery.getName());
    preview.setEmbedType(EmbedType.GALLERY);
    preview.setPreview(
      gallery.getPreview().stream().map(MediaFactory::toView).collect(Collectors.toList())
    );

    return preview;
  }

}
