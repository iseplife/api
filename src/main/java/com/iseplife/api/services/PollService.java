package com.iseplife.api.services;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dao.poll.PollFactory;
import com.iseplife.api.dto.embed.PollCreationDTO;
import com.iseplife.api.dto.embed.view.PollView;
import com.iseplife.api.entity.post.embed.poll.Poll;
import com.iseplife.api.entity.post.embed.poll.PollChoice;
import com.iseplife.api.entity.post.embed.poll.PollVote;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.dao.poll.PollChoiceRepository;
import com.iseplife.api.dao.poll.PollRepository;
import com.iseplife.api.dao.poll.PollVoteRepository;
import com.iseplife.api.exceptions.IllegalArgumentException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Guillaume on 31/07/2017.
 * back
 */
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

  public Poll getPoll(Long pollId) {
    Optional<Poll> poll = pollRepository.findById(pollId);
    if(poll.isEmpty())
      throw new IllegalArgumentException("Could not find this poll (id:" + pollId + ")");

    return poll.get();
  }

  public void addVote(Long pollId, Long pollAnswId, Long studentId) {
    Poll poll = getPoll(pollId);

    if (!poll.getMultiple()) {
      List<PollVote> voteList = pollVoteRepository.findByAnswer_Poll_IdAndStudent_Id(pollId, studentId).stream()
        .filter(votes -> !votes.getAnswer().getId().equals(pollAnswId))
        .collect(Collectors.toList());
      pollVoteRepository.deleteAll(voteList);
    }

    if (checkIsAnswered(pollAnswId, studentId)) {
      throw new IllegalArgumentException("This answer has already been chosen");
    }

    Optional<PollChoice> pollAnswer = pollChoiceRepository.findById(pollAnswId);
    if(pollAnswer.isEmpty())
      throw new IllegalArgumentException("Unknown answer");

    Student student = studentService.getStudent(studentId);
    PollVote pollVote = new PollVote();
    pollVote.setAnswer(pollAnswer.get());
    pollVote.setStudent(student);
    pollVoteRepository.save(pollVote);
  }


  private boolean checkIsAnswered(Long answerId, Long userid) {
    return pollVoteRepository.findByAnswer_IdAndStudent_Id(answerId, userid) != null;
  }

  public PollView createPoll(PollCreationDTO dto) {
    ModelMapper mapper = new ModelMapper();
    Poll poll = new Poll();

    mapper.map(dto, poll);


    poll.setChoices(new ArrayList<>());
    dto.getChoices().forEach(q -> {
      PollChoice pollChoice = new PollChoice();
      pollChoice.setContent(q);

      poll.getChoices().add(pollChoice);
    });

    return PollFactory.toView(pollRepository.save(poll));
  }



  public List<PollVote> getUserVotes(Long pollId) {
    return pollVoteRepository.findByAnswer_Poll_Id(pollId);
  }

  public List<PollVote> getVote(Long pollId, long studentId) {
    return pollVoteRepository.findByAnswer_Poll_IdAndStudent_Id(pollId, studentId);
  }

  public void removeVote(Long id, Long answerId, TokenPayload auth) {
    Poll poll = getPoll(id);
    if (poll.getEndsAt().before(new Date())) {
      throw new IllegalArgumentException("you cannot vote for this poll anymore");
    }
    PollVote vote = pollVoteRepository.findByAnswer_IdAndStudent_Id(answerId, auth.getId());
    if (vote != null) {
      pollVoteRepository.delete(vote);
    }
  }
}
