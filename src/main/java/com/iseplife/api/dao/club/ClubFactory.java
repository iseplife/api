package com.iseplife.api.dao.club;

import com.iseplife.api.dto.club.ClubAdminDTO;
import com.iseplife.api.dto.club.ClubDTO;
import com.iseplife.api.dto.club.view.ClubPreview;
import com.iseplife.api.dto.club.view.ClubView;
import com.iseplife.api.entity.club.Club;
import org.springframework.stereotype.Component;


@Component
public class ClubFactory {
  static public Club fromDTO(ClubDTO dto, Club club) {
    club.setName(dto.getName());
    club.setDescription(dto.getDescription());

    club.setWebsite(dto.getWebsite());
    club.setFacebook(dto.getFacebook());
    club.setInstagram(dto.getInstagram());

    return club;
  }

  static public Club fromAdminDTO(ClubAdminDTO dto, Club club) {
    club.setType(dto.getType());
    club.setName(dto.getName());
    club.setDescription(dto.getDescription());
    club.setCreation(dto.getCreation());

    club.setWebsite(dto.getWebsite());
    club.setFacebook(dto.getFacebook());
    club.setInstagram(dto.getInstagram());

    return club;
  }

  static public ClubView toView(Club c, Boolean canEdit, Boolean isSubscribed){
    ClubView view = new ClubView();
    view.setId(c.getId());
    view.setName(c.getName());
    view.setDescription(c.getDescription());
    view.setLogoUrl(c.getLogoUrl());
    view.setCoverUrl(c.getCoverUrl());
    view.setType(c.getType());
    view.setArchived(c.isArchived());

    view.setCanEdit(canEdit);
    view.setFeed(c.getFeed().getId());
    view.setCreation(c.getCreation());
    view.setSubscribed(isSubscribed);

    view.setInstagram(c.getInstagram());
    view.setFacebook(c.getFacebook());
    view.setWebsite(c.getWebsite());
    return view;
  }

  static public ClubPreview toPreview(Club c){
    ClubPreview view = new ClubPreview();
    view.setId(c.getId());
    view.setName(c.getName());
    view.setDescription(c.getDescription());
    view.setLogoUrl(c.getLogoUrl());

    return view;
  }

}
