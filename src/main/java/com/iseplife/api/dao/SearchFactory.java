package com.iseplife.api.dao;

import com.iseplife.api.constants.SearchItem;
import com.iseplife.api.dto.view.SearchItemView;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.user.Student;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class SearchFactory {

  public SearchItemView entityToSearchItemView(Event event) {
    SearchItemView searchItem = new SearchItemView();
    searchItem.setId(event.getId());
    searchItem.setName(event.getTitle());
    searchItem.setThumbURL(event.getImageUrl());
    searchItem.setDescription(event.getDescription());
    searchItem.setType(SearchItem.EVENT);

    // Status is false when event is passed
    searchItem.setStatus(!event.getEnd().before(new Date()));
    return searchItem;
  }

  public SearchItemView entityToSearchItemView(Club club){
    SearchItemView searchItem = new SearchItemView();
    searchItem.setId(club.getId());
    searchItem.setName(club.getName());
    searchItem.setThumbURL(club.getLogoUrl());
    searchItem.setType(SearchItem.CLUB);

    // Status is false when club is archived
    searchItem.setStatus(!club.isArchived());
    return searchItem;
  }

  public SearchItemView entityToSearchItemView(Student student){
    SearchItemView searchItem = new SearchItemView();
    searchItem.setId(student.getId());
    searchItem.setName(student.getFirstName() + " " + student.getLastName());
    searchItem.setThumbURL(student.getPicture());
    searchItem.setDescription(student.getPromo().toString());
    searchItem.setType(SearchItem.STUDENT);

    // Status is false when student is archived or promotion is passed
    searchItem.setStatus(!student.isArchived() || student.getPromo() < Calendar.getInstance().get(Calendar.YEAR));
    return searchItem;
  }

}
