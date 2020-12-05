package com.iseplife.api.services;

import com.iseplife.api.dao.poll.PollFactory;
import com.iseplife.api.dto.embed.PollCreationDTO;
import com.iseplife.api.dto.embed.view.PollChoiceView;
import com.iseplife.api.dto.embed.view.PollView;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.post.embed.poll.Poll;
import com.iseplife.api.entity.post.embed.poll.PollChoice;
import com.iseplife.api.entity.post.embed.poll.PollVote;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.dao.poll.PollChoiceRepository;
import com.iseplife.api.dao.poll.PollRepository;
import com.iseplife.api.dao.poll.PollVoteRepository;
import com.iseplife.api.exceptions.AuthException;
import com.iseplife.api.exceptions.IllegalArgumentException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PollService {
  @Autowired
  PollRepository pollRepository;

  @Autowired
  PollChoiceRepository pollChoiceRepository;

  @Autowired
  PollVoteRepository pollVoteRepository;

  @Autowired
  StudentService studentService;


  @Autowired
  PostService postService;


  public Poll getPoll(Long id) {
    Optional<Poll> poll = pollRepository.findById(id);
    if (poll.isEmpty() || !SecurityService.hasReadAccess(poll.get()))
      throw new IllegalArgumentException("Could not find this poll (id:" + id + ")");

    return poll.get();
  }

  public PollView getPollView(Long id){
    Poll poll = getPoll(id);

    return PollFactory.toView(poll);
  }

  public Poll bindPollToPost(Long id, Feed feed){
    Poll poll = getPoll(id);
    poll.setFeed(feed);

    return pollRepository.save(poll);
  }


  public void addVote(Long pollId, Long choiceId, Long studentId) {
    Poll poll = getPoll(pollId);

    if(poll.getEndsAt().before(new Date()))
      throw new AuthException("You are not allowed to vote on this poll anymore");

    if(pollVoteRepository.findByChoice_IdAndStudent_Id(choiceId, studentId).isPresent())
      throw new IllegalArgumentException("This choice has already been chosen");


    Optional<PollChoice> pollAnswer = pollChoiceRepository.findById(choiceId);
    if (pollAnswer.isEmpty())
      throw new IllegalArgumentException("Could not find this poll's choice (id:" + choiceId + ")");

    if (!poll.getMultiple()) {
      List<PollVote> voteList = pollVoteRepository.findByChoice_Poll_IdAndStudent_Id(poll.getId(), studentId).stream()
        .filter(votes -> !votes.getChoice().getId().equals(choiceId))
        .collect(Collectors.toList());
      pollVoteRepository.deleteAll(voteList);
    }


    Student student = studentService.getStudent(studentId);
    PollVote pollVote = new PollVote();
    pollVote.setChoice(pollAnswer.get());
    pollVote.setStudent(student);
    pollVoteRepository.save(pollVote);
  }


  public void removeVote(Long pollId, Long choiceId, Long studentId) {
    Poll poll = getPoll(pollId);

    if(poll.getEndsAt().before(new Date()))
      throw new AuthException("You are not allowed to vote on this poll anymore");

    Optional<PollVote> vote = pollVoteRepository.findByChoice_IdAndStudent_Id(choiceId, studentId);
    if (vote.isEmpty())
      throw new IllegalArgumentException("Could not find this answer's vote in this poll");

    pollVoteRepository.delete(vote.get());
  }


  public PollView createPoll(PollCreationDTO dto) {
    ModelMapper mapper = new ModelMapper();
    Poll poll = new Poll();

    mapper.map(dto, poll);

    poll.setCreation(new Date(

    ));
    poll.setChoices(new ArrayList<>());
    dto.getChoices().forEach(q -> {
      PollChoice pollChoice = new PollChoice();
      pollChoice.setContent(q);

      poll.getChoices().add(pollChoice);
    });

    return PollFactory.toView(pollRepository.save(poll));
  }


  public List<PollChoiceView> getPollVotes(Long pollId) {
    Poll poll = getPoll(pollId);

    return poll.getChoices().stream()
      .map(choice -> poll.getAnonymous() ? PollFactory.toAnonymousView(choice): PollFactory.toView(choice))
      .collect(Collectors.toList());
  }

}
