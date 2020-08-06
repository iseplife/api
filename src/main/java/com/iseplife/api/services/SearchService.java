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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SearchService {

  //TODO: potentially paginate results
  private static final int RESULTS_PER_PAGE = 10;
  @Autowired
  StudentRepository studentRepository;

  @Autowired
  EventRepository eventRepository;

  @Autowired
  ClubRepository clubRepository;

  @Autowired
  SearchFactory searchFactory;

  public Page<SearchItemView> globalSearch(String filter, Integer page, Boolean returnAll) {
    try {
      CompletableFuture<List<SearchItemView>> userAsync = CompletableFuture.supplyAsync(() ->
        searchUser(filter, "", true,  page, returnAll).getContent()
      );

      CompletableFuture<List<SearchItemView>> eventAsync = CompletableFuture.supplyAsync(() ->
        searchEvent(filter, page, returnAll).getContent()
      );

      CompletableFuture<List<SearchItemView>> clubAsync = CompletableFuture.supplyAsync(() ->
        searchClub(filter, page, returnAll).getContent()
      );

      CompletableFuture<Void> promiseAllAsync = CompletableFuture.allOf(userAsync, eventAsync, clubAsync);

      promiseAllAsync.get();

      List<SearchItemView> globalSearchToList = Stream.of(userAsync.get(), eventAsync.get(), clubAsync.get())
        .flatMap(Collection::stream)
        .collect(Collectors.toList());

      return new PageImpl<>(globalSearchToList, PageRequest.of(page, RESULTS_PER_PAGE), globalSearchToList.size());

    } catch (ExecutionException | InterruptedException e) {
      e.printStackTrace();
      return null;
    }
  }

  public Page<SearchItemView> searchUser(String filter, String promos, Boolean atoz, Integer page, Boolean returnAll) {
    Page<Student> students;
    PageRequest pr = PageRequest.of(
      page, RESULTS_PER_PAGE,
      Sort.by(Sort.Direction.DESC, "promo").and(
        Sort.by(atoz ? Sort.Direction.ASC: Sort.Direction.DESC, "lastName")
      )
    );
    if (!promos.isEmpty()) {
      List<Integer> promoArray = Arrays.stream(promos.split(","))
        .map(Integer::parseInt)
        .collect(Collectors.toList());
      students = studentRepository.searchStudent(filter, promoArray, pr);
    } else {
      students = studentRepository.searchStudent(filter, pr);
    }

    return students.map(s -> searchFactory.entityToSearchItemView(s));
  }

  public Page<SearchItemView> searchEvent(String filter, int page, Boolean returnAll) {
    Page<Event> events = eventRepository.searchEvent(filter, PageRequest.of(page, RESULTS_PER_PAGE));

    return events.map(e -> searchFactory.entityToSearchItemView(e));
  }

  public Page<SearchItemView> searchClub(String filter, int page, Boolean returnAll) {
    Page<Club> clubs = clubRepository.findAllByNameContainingIgnoringCase(filter, PageRequest.of(page, RESULTS_PER_PAGE));

    return clubs.map(c -> searchFactory.entityToSearchItemView(c));
  }
}
