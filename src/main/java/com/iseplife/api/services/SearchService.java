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

  //TODO: potentially paginate result

  public List<SearchItemView> searchUserPaged(String filter, String promos, Boolean returnAll) {

    List<Student> students;
    if (!promos.isEmpty()) {
      List<String> promoArray = Arrays.stream(promos.split(","))
              .collect(Collectors.toList());
      students = studentRepository.searchStudent(filter, promoArray);
    } else {
      students = studentRepository.searchStudent(filter);
    }

    return students.stream()
            .map(s -> searchFactory.entityToSearchItemView(s))
            .collect(Collectors.toList());
  }

  public List<SearchItemView> searchUser(String filter) {
    return studentRepository.searchStudent(filter)
      .stream()
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
