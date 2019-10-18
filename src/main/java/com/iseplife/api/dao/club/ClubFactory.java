package com.iseplife.api.dao.club;

import com.iseplife.api.dto.ClubDTO;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.dto.ClubDTO;
import com.iseplife.api.entity.club.Club;
import org.springframework.stereotype.Component;

/**
 * Created by Guillaume on 30/07/2017.
 * back
 */
@Component
public class ClubFactory {
  public Club dtoToEntity(ClubDTO dto) {
    Club club = new Club();
    club.setName(dto.getName());
    club.setDescription(dto.getDescription());
    club.setCreatedAt(dto.getCreation());
    club.setWebsite(dto.getWebsite());
    return club;
  }
}
