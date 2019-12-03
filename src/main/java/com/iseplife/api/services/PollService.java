package com.iseplife.api.services;

import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.dto.media.PollCreationDTO;
import com.iseplife.api.entity.post.embed.poll.Poll;
import com.iseplife.api.entity.post.embed.poll.PollAnswer;
import com.iseplife.api.entity.post.embed.poll.PollVote;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.constants.PublishStateEnum;
import com.iseplife.api.dao.poll.PollAnswerRepository;
import com.iseplife.api.dao.poll.PollRepository;
import com.iseplife.api.dao.poll.PollVoteRepository;
import com.iseplife.api.exceptions.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
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
  PollAnswerRepository pollAnswerRepository;

  @Autowired
  PollVoteRepository pollVoteRepository;

  @Autowired
  StudentService studentService;

  @Autowired
  AuthService authService;

  @Autowired
  PostService postService;

  public void addVote(Long pollId, Long pollAnswId, Long studentId) {
    Poll poll = getPoll(pollId);

    if (!poll.getMultiAnswers()) {
      List<PollVote> voteList = pollVoteRepository.findByAnswer_Poll_IdAndStudent_Id(pollId, studentId).stream()
        .filter(votes -> !votes.getAnswer().getId().equals(pollAnswId))
        .collect(Collectors.toList());
      pollVoteRepository.delete(voteList);
    }

    if (checkIsAnswered(pollAnswId, studentId)) {
      throw new IllegalArgumentException("This answer has already been chosen");
    }

    PollAnswer pollAnswer = pollAnswerRepository.findOne(pollAnswId);
    Student student = studentService.getStudent(studentId);
    PollVote pollVote = new PollVote();
    pollVote.setAnswer(pollAnswer);
    pollVote.setStudent(student);
    pollVoteRepository.save(pollVote);
  }

  private boolean checkHasEnded(Date date) {
   return date.before(new Date());
  }

  private boolean checkIsAnswered(Long answerId, Long userid) {
    return pollVoteRepository.findByAnswer_IdAndStudent_Id(answerId, userid) != null;
  }

  public Poll createPoll(Long postId, PollCreationDTO pollDTO) {
    // Create poll
    Poll poll = new Poll();
    poll.setName(pollDTO.getTitle());
    poll.setEndDate(pollDTO.getEndDate());
    poll.setMultiAnswers(pollDTO.getMultiAnswers());
    Poll saved = pollRepository.save(poll);

    postService.addMediaEmbed(postId, saved);

    // Add answers
    pollDTO.getAnswers().forEach(q -> {
      PollAnswer pollAnswer = new PollAnswer();
      pollAnswer.setPoll(poll);
      pollAnswer.setContent(q);
      pollAnswerRepository.save(pollAnswer);
    });

    postService.setPublishState(postId, PublishStateEnum.PUBLISHED);
    return pollRepository.findOne(saved.getId());
  }

  public Poll getPoll(Long pollId) {
    Poll poll = pollRepository.findOne(pollId);
    if (authService.isUserAnonymous()) {
      throw new AuthException("you can't access this poll");
    }
    return poll;
  }

  public List<PollVote> getUserVotes(Long pollId) {
    return pollVoteRepository.findByAnswer_Poll_Id(pollId);
  }

  public List<PollVote> getVote(Long pollId, long studentId) {
    return pollVoteRepository.findByAnswer_Poll_IdAndStudent_Id(pollId, studentId);
  }

  public void removeVote(Long id, Long answerId, TokenPayload auth) {
    Poll poll = getPoll(id);
    if (checkHasEnded(poll.getEndDate())) {
      throw new IllegalArgumentException("you cannot vote for this poll anymore");
    }
    PollVote vote = pollVoteRepository.findByAnswer_IdAndStudent_Id(answerId, auth.getId());
    if (vote != null) {
      pollVoteRepository.delete(vote);
    }
  }
}
