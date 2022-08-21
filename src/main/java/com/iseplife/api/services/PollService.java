package com.iseplife.api.services;

import com.iseplife.api.dao.poll.PollFactory;
import com.iseplife.api.dto.poll.PollChoiceDTO;
import com.iseplife.api.dto.poll.PollCreationDTO;
import com.iseplife.api.dto.poll.PollEditionDTO;
import com.iseplife.api.dto.poll.view.PollChoiceView;
import com.iseplife.api.dto.poll.view.PollView;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.post.embed.poll.Poll;
import com.iseplife.api.entity.post.embed.poll.PollChoice;
import com.iseplife.api.entity.post.embed.poll.PollVote;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.dao.poll.PollChoiceRepository;
import com.iseplife.api.dao.poll.PollRepository;
import com.iseplife.api.dao.poll.PollVoteRepository;
import com.iseplife.api.dao.post.PostRepository;
import com.iseplife.api.exceptions.http.HttpForbiddenException;
import com.iseplife.api.exceptions.http.HttpBadRequestException;
import com.iseplife.api.exceptions.http.HttpNotFoundException;
import com.iseplife.api.websocket.services.WSPostService;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PollService {
  @Lazy final private StudentService studentService;
  @Lazy final private PostService postService;
  final private ModelMapper mapper;
  final private PollRepository pollRepository;
  final private PostRepository postRepository;
  final private PollChoiceRepository pollChoiceRepository;
  final private PollVoteRepository pollVoteRepository;
  final private WSPostService wsPostService;
  final private PollFactory pollFactory;

  public Poll getPoll(Long id) {
    return getPoll(id, null);
  }
  public Poll getPoll(Long id, Feed proposedPollFeed) {
    Optional<Poll> poll = pollRepository.findById(id);
    if (poll.isEmpty() || !SecurityService.hasReadAccess(poll.get().getFeed() == null ? proposedPollFeed : poll.get().getFeed()))
      throw new HttpNotFoundException("poll_not_found");

    return poll.get();
  }

  public PollView getPollView(Long id, Long studentId) {
    Poll poll = getPoll(id);

    return pollFactory.toView(poll, studentId);
  }

  public Poll bindPollToPost(Long id, Feed feed) {
    Poll poll = getPoll(id);
    poll.setFeed(feed);

    return pollRepository.save(poll);
  }


  public List<PollChoiceView> addVote(Long pollId, Long choiceId, Long studentId) {
    Poll poll = getPoll(pollId);

    if (poll.getEndsAt().before(new Date()))
      throw new HttpBadRequestException("poll_finished");

    if (pollVoteRepository.findByChoice_IdAndStudent_Id(choiceId, studentId).isPresent())
      throw new HttpBadRequestException("choice_already_picked");


    Optional<PollChoice> pollAnswer = pollChoiceRepository.findById(choiceId);
    if (pollAnswer.isEmpty())
      throw new HttpNotFoundException("poll_not_found");
    
    PollChoice pollChoice = pollAnswer.get();

    if (!poll.isMultiple()) {
      List<PollVote> voteList = pollVoteRepository.findByChoice_Poll_IdAndStudent_Id(poll.getId(), studentId).stream()
        .filter(votes -> !votes.getChoice().getId().equals(choiceId))
        .collect(Collectors.toList());
      
      pollVoteRepository.deleteAll(voteList);
    }


    Student student = studentService.getStudent(studentId);
    
    PollVote pollVote = new PollVote();
    pollVote.setChoice(pollChoice);
    pollVote.setStudent(student);
    
    pollVoteRepository.save(pollVote);
    
    wsPostService.broadcastPollChange(postRepository.findPostIdByEmbed(poll), pollChoiceRepository.findAllByPoll(poll));

    return pollChoiceRepository.findAllByPoll(poll, student)
        .stream()
        .map(pollFactory::toView)
        .collect(Collectors.toList());
  }


  public List<PollChoiceView> removeVote(Long pollId, Long choiceId, Long studentId) {
    Poll poll = getPoll(pollId);

    if (poll.getEndsAt().before(new Date()))
      throw new HttpBadRequestException("poll_finished");

    Optional<PollVote> vote = pollVoteRepository.findByChoice_IdAndStudent_Id(choiceId, studentId);
    if (vote.isEmpty())
      throw new HttpBadRequestException("student_answer_not_found");

    pollVoteRepository.delete(vote.get());
    
    wsPostService.broadcastPollChange(postRepository.findPostIdByEmbed(poll), pollChoiceRepository.findAllByPoll(poll));
    
    return pollChoiceRepository.findAllByPoll(poll, studentId)
        .stream()
        .map(pollFactory::toView)
        .collect(Collectors.toList());
  }


  public PollView createPoll(PollCreationDTO dto, Long studentId) {
    Poll poll = new Poll();

    mapper.map(dto, poll);

    poll.setCreation(new Date());

    poll.setChoices(new ArrayList<>());
    dto.getChoices().forEach(choice -> {
      PollChoice pollChoice = new PollChoice();
      pollChoice.setContent(choice.getContent());
      pollChoice.setPoll(poll);

      poll.getChoices().add(pollChoice);
    });

    return pollFactory.toView(pollRepository.save(poll), studentId);
  }

  public void deletePoll(Poll poll){
    pollRepository.delete(poll);
  }

  public PollView updatePoll(PollEditionDTO dto, Long studentId) {
    Poll poll = getPoll(dto.getId());

    if (!SecurityService.hasRightOn(postService.getPostFromEmbed(poll)))
      throw new HttpForbiddenException("insufficient_rights");

    poll.setAnonymous(dto.isAnonymous());
    poll.setMultiple(dto.isMultiple());
    poll.setEndsAt(dto.getEndsAt());

    // Edit or remove existing choices
    new ArrayList<>(poll.getChoices()).forEach(choice -> {
      Optional<PollChoiceDTO> dq = dto.getChoices().stream()
        .filter(c -> choice.getId().equals(c.getId()))
        .findAny();

      if (!dq.isPresent()) {
        poll.getChoices().remove(choice);
        pollChoiceRepository.delete(choice);
      } else
        dto.getChoices().remove(dq.get());
    });

    // Add all new choices
    dto.getChoices().forEach(q -> {
      PollChoice pollChoice = new PollChoice();
      pollChoice.setContent(q.getContent());
      pollChoice.setPoll(poll);

      poll.getChoices().add(pollChoice);
    });
    
    poll.getChoices().forEach(choice -> System.out.println(choice.getContent()));

    return pollFactory.toView(pollRepository.save(poll), studentId);
  }


  public List<PollChoiceView> getPollVotes(Long pollId, Long studentId) {
    return pollChoiceRepository.findAllByPoll(pollId, studentId)
        .stream()
        .map(pollFactory::toView)
        .collect(Collectors.toList());
  }

}
