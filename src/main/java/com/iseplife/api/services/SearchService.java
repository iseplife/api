package com.iseplife.api.services;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dao.SearchFactory;
import com.iseplife.api.dao.club.ClubRepository;
import com.iseplife.api.dao.event.EventRepository;
import com.iseplife.api.dao.group.GroupRepository;
import com.iseplife.api.dao.student.StudentRepository;
import com.iseplife.api.dto.view.SearchItemView;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.event.Event;
import com.iseplife.api.entity.group.Group;
import com.iseplife.api.entity.user.Student;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class SearchService {
  final private StudentRepository studentRepository;
  final private EventRepository eventRepository;
  final private ClubRepository clubRepository;
  final private GroupRepository groupRepository;

  final private static int RESULTS_PER_PAGE = 18;


  public Page<SearchItemView> globalSearch(String filter, Integer page, Boolean returnAll, TokenPayload token) {
    try {
      CompletableFuture<List<SearchItemView>> userAsync = CompletableFuture.supplyAsync(() ->
        searchUser(filter, "", true, page).getContent()
      );

      CompletableFuture<List<SearchItemView>> eventAsync = CompletableFuture.supplyAsync(() ->
        searchEvent(filter, page, returnAll, token, 0).getContent()
      );

      CompletableFuture<List<SearchItemView>> clubAsync = CompletableFuture.supplyAsync(() ->
        searchClub(filter, page, returnAll).getContent()
      );

      CompletableFuture<List<SearchItemView>> groupAsync = CompletableFuture.supplyAsync(() ->
        searchGroup(filter, page, returnAll, token).getContent()
      );

      CompletableFuture<Void> promiseAllAsync = CompletableFuture.allOf(clubAsync, userAsync, groupAsync, eventAsync);

      promiseAllAsync.get();
      List<SearchItemView> globalSearchToList = Stream.of(clubAsync.get(), userAsync.get(), groupAsync.get(), eventAsync.get())
        .flatMap(Collection::stream)
        .collect(Collectors.toList());

      return new PageImpl<>(globalSearchToList, PageRequest.of(page, RESULTS_PER_PAGE), globalSearchToList.size());

    } catch (ExecutionException | InterruptedException e) {
      e.printStackTrace();
      return null;
    }
  }

  public List<SearchItemView> searchUserAll(String name) {
    return studentRepository.searchStudent(name.toLowerCase(), true)
      .stream()
      .map(SearchFactory::toSearchItemView)
      .collect(Collectors.toList());
  }

  public Page<SearchItemView> searchUser(String filter, String promos, Boolean atoz, Integer page) {
    Page<Student> students;
    PageRequest pr = PageRequest.of(
      page, RESULTS_PER_PAGE,
      Sort.by(Sort.Direction.DESC, "promo").and(
        Sort.by(atoz ? Sort.Direction.ASC : Sort.Direction.DESC, "lastName")
      )
    );
    if (!promos.isEmpty()) {
      List<Integer> promoArray = Arrays.stream(promos.split(","))
        .map(Integer::parseInt)
        .collect(Collectors.toList());
      students = studentRepository.searchStudent(filter.toLowerCase(), promoArray, pr);
    } else {
      students = studentRepository.searchStudent(filter.toLowerCase(), pr);
    }

    return students.map(SearchFactory::toSearchItemView);
  }

  public Page<SearchItemView> searchEvent(String filter, int page, Boolean returnAll, TokenPayload token, int minYear) {
    Page<Event> events = eventRepository.searchEventAfterYear(
      filter.toLowerCase(),
      token.getRoles().contains("ROLE_ADMIN"),
      token.getFeeds(),
      minYear,
      PageRequest.of(page, RESULTS_PER_PAGE)
    );

    return events.map(SearchFactory::toSearchItemView);
  }

  public Page<SearchItemView> searchClub(String filter, int page, Boolean returnAll) {
    Page<Club> clubs = clubRepository.findAllByNameContainingIgnoringCase(
      filter.toLowerCase(),
      PageRequest.of(page, RESULTS_PER_PAGE)
    );

    return clubs.map(SearchFactory::toSearchItemView);
  }

  public Page<SearchItemView> searchGroup(String filter, int page, Boolean returnAll, TokenPayload token) {
    Page<Group> groups = groupRepository.searchGroup(
      filter.toLowerCase(),
      token.getId(),
      token.getRoles().contains("ROLE_ADMIN"),
      PageRequest.of(page, RESULTS_PER_PAGE)
    );

    return groups.map(SearchFactory::toSearchItemView);
  }
}
