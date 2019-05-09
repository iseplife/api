package com.iseplive.api.controllers;

import com.iseplive.api.conf.jwt.TokenPayload;
import com.iseplive.api.constants.Roles;
import com.iseplive.api.dto.EmployeeDTO;
import com.iseplive.api.dto.dor.DorConfigDTO;
import com.iseplive.api.dto.dor.QuestionDorDTO;
import com.iseplive.api.dto.dor.SessionDorDTO;
import com.iseplive.api.dto.dor.VoteDorDTO;
import com.iseplive.api.dto.view.AnswerDorDTO;
import com.iseplive.api.entity.dor.EventDor;
import com.iseplive.api.entity.dor.QuestionDor;
import com.iseplive.api.entity.dor.SessionDor;
import com.iseplive.api.entity.dor.VoteDor;
import com.iseplive.api.entity.user.Employee;
import com.iseplive.api.exceptions.NotFoundException;
import com.iseplive.api.services.DorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Map;

/**
 * Created by Guillaume on 10/02/2018.
 * back
 */
@RestController
@RequestMapping("/dor")
public class DorController {

  @Autowired
  DorService dorService;

  @PostMapping("/session")
  @RolesAllowed({ Roles.ADMIN })
  public SessionDor createSession(@RequestBody SessionDorDTO sessionDorDTO) {
    return dorService.createSession(sessionDorDTO);
  }

  @GetMapping("/session")
  @RolesAllowed({ Roles.STUDENT })
  public List<SessionDor> getSessions() {
    return dorService.getSessions();
  }

  @GetMapping("/session/current")
  @RolesAllowed({ Roles.STUDENT })
  public SessionDor getCurrentSession() {
    return dorService.getCurrentSession();
  }

  @GetMapping("/session/current/round/{round}")
  @RolesAllowed({ Roles.STUDENT })
  public Map<Long, List<AnswerDorDTO>> getRoundWinner(@PathVariable int round) {
    SessionDor sessionDor = dorService.getCurrentSession();
    if (sessionDor == null) {
      throw new NotFoundException("no session currently available");
    }
    if (round == 1) {
      return dorService.computeFirstRoundWinners(sessionDor.getId());
    }
    if (round == 2) {
      return dorService.computeFinalResults(sessionDor.getId());
    }
    throw new NotFoundException("round not available");
  }

  @GetMapping("/session/{sessionId}/round/{rnd}/question/{questionId}")
  @RolesAllowed({ Roles.ADMIN })
  public List<AnswerDorDTO> getSortedAnswersForQuestion(@PathVariable Long sessionId,
                                                  @PathVariable int rnd,
                                                  @PathVariable Long questionId) {
    return dorService.getAnswers(sessionId, rnd, questionId);
  }

  @DeleteMapping("/session/{id}")
  @RolesAllowed({ Roles.ADMIN })
  public void deleteSession(@PathVariable Long id) {
    dorService.deleteSession(id);
  }

  @PutMapping("/session/{id}")
  @RolesAllowed({ Roles.ADMIN })
  public SessionDor updateSession(@PathVariable Long id, @RequestBody SessionDor dorSession) {
    return dorService.updateSession(id, dorSession);
  }

  @PutMapping("/session/{id}/enable")
  @RolesAllowed({ Roles.ADMIN })
  public void toggleEnableSession(@PathVariable Long id) {
    dorService.toggleSession(id);
  }

  @GetMapping("/session/{id}/diploma")
  @RolesAllowed({ Roles.ADMIN })
  public void generateDiploma(@PathVariable Long id) {
    dorService.generateDiploma(id);
  }


  @GetMapping("/question")
  @RolesAllowed({ Roles.STUDENT })
  public List<QuestionDor> getQuestions() {
    return dorService.getQuestions();
  }

  @PostMapping("/question")
  @RolesAllowed({ Roles.ADMIN })
  public QuestionDor createQuestion(@RequestBody QuestionDorDTO questionDorDTO) {
    return dorService.createQuestion(questionDorDTO);
  }

  @PutMapping("/question/{id}")
  @RolesAllowed({ Roles.ADMIN })
  public QuestionDor updateQuestion(@PathVariable Long id, @RequestBody QuestionDor questionDor) {
    return dorService.updateQuestion(id, questionDor);
  }

  @DeleteMapping("/question/{id}")
  @RolesAllowed({ Roles.ADMIN })
  public void deleteQuestion(@PathVariable Long id) {
    dorService.deleteQuestion(id);
  }

  @GetMapping("/vote/round/{round}")
  @RolesAllowed({ Roles.STUDENT })
  public List<VoteDor> getCurrentVotes(@PathVariable int round, @AuthenticationPrincipal TokenPayload payload) {
    return dorService.getCurrentVotes(payload.getId(), round);
  }

  @PutMapping("/vote")
  @RolesAllowed({ Roles.STUDENT })
  public VoteDor vote(@RequestBody VoteDorDTO voteDor, @AuthenticationPrincipal TokenPayload payload) {
    return dorService.handleVote(voteDor, payload);
  }

  @GetMapping("/event")
  @RolesAllowed({ Roles.STUDENT })
  public List<EventDor> getEvents() {
    return dorService.getEvents();
  }

  @GetMapping("/event/search")
  @RolesAllowed({ Roles.STUDENT })
  public List<EventDor> searchEvents(@RequestParam String name) {
    return dorService.searchEvent(name);
  }

  @PostMapping("/event")
  @RolesAllowed({ Roles.ADMIN })
  public EventDor createEvent(@RequestBody EventDor event) {
    return dorService.createEvent(event);
  }

  @DeleteMapping("/event/{id}")
  @RolesAllowed({ Roles.ADMIN })
  public void deleteEvent(@PathVariable Long id) {
    dorService.deleteEvent(id);
  }

  @PutMapping("/event/{id}")
  @RolesAllowed({ Roles.ADMIN })
  public EventDor updateEvent(@PathVariable Long id, @RequestBody EventDor event) {
    return dorService.updateEvent(id, event);
  }

  @GetMapping("/employee/search")
  @RolesAllowed({ Roles.STUDENT })
  public List<Employee> searchEmployees(@RequestParam String name) {
    return dorService.searchEmployee(name);
  }

  @GetMapping("/employee")
  @RolesAllowed({ Roles.STUDENT })
  public List<Employee> getEmployees() {
    return dorService.getEmployees();
  }

  @PostMapping("/employee")
  @RolesAllowed({ Roles.STUDENT })
  public Employee createEmployee(@RequestBody EmployeeDTO employee) {
    return dorService.createEmployee(employee);
  }

  @PutMapping("/employee/{id}")
  @RolesAllowed({ Roles.STUDENT })
  public Employee updateEmployee(@PathVariable Long id, @RequestBody EmployeeDTO employee) {
    return dorService.updateEmployee(id, employee);
  }

  @DeleteMapping("/employee/{id}")
  @RolesAllowed({ Roles.STUDENT })
  public void deleteEmployee(@PathVariable Long id) {
    dorService.deleteEmployee(id);
  }

  @GetMapping("/config")
  public DorConfigDTO getDorConfig() {
    return dorService.readDorConfig();
  }

  @PutMapping("/config")
  @RolesAllowed({ Roles.ADMIN })
  public void updateDorConfig(@RequestBody DorConfigDTO configDTO) {
    dorService.updateDorConfig(configDTO);
  }

  @PutMapping("/config/diploma")
  @RolesAllowed({ Roles.ADMIN })
  public void updateDorDiploma(@RequestParam MultipartFile diploma) {
    dorService.updateDiploma(diploma);
  }

  @PutMapping("/config/font")
  @RolesAllowed({ Roles.ADMIN })
  public void updateDorDiplomaFont(@RequestParam MultipartFile font) {
    dorService.updateDiplomaFont(font);
  }
}
