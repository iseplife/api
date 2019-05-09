package com.iseplive.api.dao.club;

import com.iseplive.api.dto.ClubDTO;
import com.iseplive.api.entity.club.Club;
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
    club.setCreation(dto.getCreation());
    club.setWebsite(dto.getWebsite());
    return club;
  }
}
