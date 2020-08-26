package com.iseplife.api.dao.gallery;

import com.iseplife.api.dao.club.ClubFactory;
import com.iseplife.api.dto.gallery.view.GalleryPreview;
import com.iseplife.api.dto.gallery.view.GalleryView;
import com.iseplife.api.entity.post.embed.Gallery;
import com.iseplife.api.services.AuthService;

public class GalleryFactory {

  static public GalleryView toView(Gallery gallery){
    GalleryView view = new GalleryView();
    view.setId(gallery.getId());
    view.setName(gallery.getName());
    view.setCreation(gallery.getCreation());
    view.setImages(gallery.getImages());
    view.setClub(ClubFactory.toPreview(gallery.getClub()));
    view.setHasRight(AuthService.hasRightOn(gallery));

    return view;
  }

  static public GalleryPreview toPreview(Gallery gallery){
    GalleryPreview preview = new GalleryPreview();
    preview.setId(gallery.getId());
    preview.setName(gallery.getName());
    preview.setPreview(gallery.getPreviewImages());

    return preview;
  }

}
