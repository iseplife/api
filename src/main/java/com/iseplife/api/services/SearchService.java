package com.iseplife.api.services;

import com.iseplife.api.dao.SearchFactory;
import com.iseplife.api.dao.club.ClubRepository;
import com.iseplife.api.dao.event.EventRepository;
import com.iseplife.api.dao.student.StudentRepository;
import com.iseplife.api.dto.view.SearchItemView;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.user.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SearchService {

  @Autowired
  StudentRepository studentRepository;

  @Autowired
  EventRepository eventRepository;

  @Autowired
  ClubRepository clubRepository;

  @Autowired
  SearchFactory searchFactory;

  //TODO: potentially paginate results

  public List<SearchItemView> globalSearch(String filter, String promos, Boolean returnAll) {
    return Stream
            .of(searchUser(filter, promos, returnAll), searchEvent(filter, returnAll), searchClub(filter, returnAll))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
  }

  public List<SearchItemView> searchUser(String filter, String promos, Boolean returnAll) {

    List<String> promoArray = null;
    if (!promos.isEmpty()) {
      promoArray = Arrays.stream(promos.split(","))
              .collect(Collectors.toList());
    }
    List<Student> students = studentRepository.searchStudent(filter, promoArray);

    return students.stream()
            .map(s -> searchFactory.entityToSearchItemView(s))
            .collect(Collectors.toList());
  }

  public List<SearchItemView> searchEvent(String filter, Boolean returnAll) {
    List<Event> events = eventRepository.searchEvent(filter);

    return events.stream()
      .map(e -> searchFactory.entityToSearchItemView(e))
      .collect(Collectors.toList());
  }

  public List<SearchItemView> searchClub(String filter, Boolean returnAll) {
    List<Club> clubs = clubRepository.findAllByNameContainingIgnoringCase(filter);

    return clubs.stream()
      .map(c -> searchFactory.entityToSearchItemView(c))
      .collect(Collectors.toList());
  }
}
