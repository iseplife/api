package com.iseplife.api.services;

import com.iseplife.api.conf.StorageConfig;
import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.FeedType;
import com.iseplife.api.constants.GroupType;
import com.iseplife.api.dao.group.GroupFactory;
import com.iseplife.api.dao.group.GroupMemberRepository;
import com.iseplife.api.dao.group.GroupRepository;
import com.iseplife.api.dto.group.GroupCreationDTO;
import com.iseplife.api.dto.group.GroupMemberDTO;
import com.iseplife.api.dto.group.GroupUpdateDTO;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.group.Group;
import com.iseplife.api.entity.group.GroupMember;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.exceptions.http.HttpForbiddenException;
import com.iseplife.api.exceptions.http.HttpBadRequestException;
import com.iseplife.api.exceptions.http.HttpNotFoundException;
import com.iseplife.api.services.fileHandler.FileHandler;
import com.iseplife.api.websocket.services.WSGroupService;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GroupService {
  @Lazy final private StudentService studentService;
  final private ModelMapper mapper;
  final private GroupRepository groupRepository;
  final private GroupMemberRepository groupMemberRepository;

  final private SubscriptionService subService;
  final private WSGroupService wsGroupService;
  final private GroupFactory groupFactory;

  @Qualifier("FileHandlerBean") final private FileHandler fileHandler;

  final private static int RESULTS_PER_PAGE = 20;

  public Group getGroup(Long id) {
    Optional<Group> group = groupRepository.findById(id);
    if (group.isEmpty() || (group.get().isRestricted() && !groupMemberRepository.isMemberOfGroup(id, SecurityService.getLoggedId())))
      throw new HttpNotFoundException("gallery_not_found");

    return group.get();
  }

  public List<GroupMember> getGroupAdminMembers(Long id) {
    return groupMemberRepository.findByGroup_IdAndAdminIsTrue(id);
  }

  public List<GroupMember> getGroupMembers(Long id) {
    return groupMemberRepository.findByGroup_Id(id);
  }

  public GroupMember getGroupMember(Long id) {
    Optional<GroupMember> member = groupMemberRepository.findById(id);
    if (member.isEmpty())
      throw new HttpNotFoundException("member_not_found");

    return member.get();
  }



  public Page<Group> getAll(int page) {
    return groupRepository.findAll(PageRequest.of(page, RESULTS_PER_PAGE));
  }

  public List<Group> getUserGroups(TokenPayload token) {
    return groupRepository.findAllUserGroups(token.getId());
  }


  public Group createGroup(GroupCreationDTO dto) {
    Group group = mapper.map(dto, Group.class);

    group.setFeed(new Feed(group.getName(), FeedType.GROUP));

    group.setMembers(createGroupAdminMembers(dto.getAdmins(), group));

    group = groupRepository.save(group);

    for(GroupMember member : group.getMembers()) {
      subService.subscribe(group, member.getStudent(), true);
      wsGroupService.sendJoin(groupFactory.toPreview(group), member.getStudent());
    }

    return group;
  }

  private List<GroupMember> createGroupAdminMembers(List<Long> ids, Group group) {
    List<GroupMember> adminMembers = new ArrayList<>();
    studentService.getStudents(ids).forEach(a -> {
      GroupMember member = new GroupMember();
      member.setAdmin(true);
      member.setStudent(a);
      member.setGroup(group);

      adminMembers.add(member);
    });

    return adminMembers;
  }


  public Group updateGroup(Long id, GroupUpdateDTO dto) {
    Group group = getGroup(id);
    mapper.map(dto, group);

    // Keep only admins that are in dto.admins
    for(GroupMember m : group.getMembers())
      if (dto.getAdmins().contains(m.getStudent().getId())) {
        // In case the member wasn't already an admin
        m.setAdmin(true);

        dto.getAdmins().remove(m.getStudent().getId());
      }

    // We create a group member admin for all leftovers ids
    group.getMembers().addAll(createGroupAdminMembers(dto.getAdmins(), group));

    return groupRepository.save(group);
  }

  public String updateCover(Long id, MultipartFile cover) {
    Group group = getGroup(id);
    if (!SecurityService.hasRightOn(group))
      throw new HttpForbiddenException("insufficient_rights");

    if (group.getCover() != null)
      fileHandler.delete(group.getCover());

    if (cover == null) {
      group.setCover(null);
    } else {
      Map params = Map.of(
        "process", "compress",
        "sizes", StorageConfig.MEDIAS_CONF.get("feed_cover").sizes
      );
      group.setCover(fileHandler.upload(cover, StorageConfig.MEDIAS_CONF.get("feed_cover").path, false, params));
    }

    groupRepository.save(group);
    return group.getCover();
  }

  public Boolean toggleArchive(Long id) {
    Group group = getGroup(id);
    if (group.getType() != GroupType.DEFAULT)
      throw new HttpBadRequestException("archiving_impossible");


    group.setArchivedAt(group.isArchived() ? null : new Date());
    groupRepository.save(group);

    return group.isArchived();
  }

  public void deleteGroup(Long id) {
    Group group = getGroup(id);
    if (group.getType() != GroupType.DEFAULT) {
      throw new HttpBadRequestException("deletion_impossible");
    }

    for(GroupMember member : group.getMembers()) {
      subService.unsubscribe(group.getId(), member.getStudent().getId());
      wsGroupService.sendLeave(group.getId(), member.getStudent());
    }

    groupRepository.delete(group);
  }


  public Boolean promoteMember(Long id, Long member) {
    GroupMember groupMember = getGroupMember(member);
    if (!SecurityService.hasRightOn(groupMember.getGroup()))
      throw new HttpForbiddenException("insufficient_rights");

    groupMember.setAdmin(true);
    groupMemberRepository.save(groupMember);

    return true;
  }

  public Boolean demoteMember(Long id, Long member) {
    GroupMember groupMember = getGroupMember(member);
    if (!SecurityService.hasRightOn(groupMember.getGroup()))
      throw new HttpForbiddenException("insufficient_rights");

    if (groupMember.isAdmin() && groupMemberRepository.findGroupAdminCount(groupMember.getGroup()) < 1)
      throw new HttpBadRequestException("minimum_admins_size_required");

    groupMember.setAdmin(false);
    groupMemberRepository.save(groupMember);
    return true;
  }

  public GroupMember addMember(Long id, GroupMemberDTO dto) {
    return addMember(getGroup(id), dto, false);
  }
  public GroupMember addMember(Group group, GroupMemberDTO dto, boolean force) {
    if(!force) {
      if (!SecurityService.hasRightOn(group))
        throw new HttpForbiddenException("insufficient_rights");
    }

    if(groupMemberRepository.isMemberOfGroup(group.getId(), dto.getStudentId()))
      throw new HttpBadRequestException("student_already_in_group");

    System.out.println(SecurityService.getLoggedId()+" added "+dto.getStudentId()+" to group "+group.getName());

    GroupMember member = new GroupMember();
    member.setAdmin(false);
    member.setStudent(studentService.getStudent(dto.getStudentId()));
    member.setGroup(group);

    member = groupMemberRepository.save(member);

    subService.subscribe(group, member.getStudent(), true);
    wsGroupService.sendJoin(groupFactory.toPreview(group), member.getStudent());
    return member;
  }

  public Boolean removeMember(Long id, Long member) {
    GroupMember groupMember = getGroupMember(member);
    if (!SecurityService.hasRightOn(groupMember.getGroup()))
      throw new HttpForbiddenException("insufficient_rights");

    if (groupMember.isAdmin() && groupMemberRepository.findGroupAdminCount(groupMember.getGroup()) < 1)
      throw new HttpBadRequestException("minimum_admins_size_required");

    subService.unsubscribe(groupMember.getGroup().getId(), groupMember.getStudent().getId());
    wsGroupService.sendLeave(groupMember.getGroup().getId(), groupMember.getStudent());

    groupMemberRepository.delete(groupMember);
    return true;
  }

  public void addToPromoGroup(Student student) {
    Optional<Group> optional = groupRepository.findOneByName("Promo "+student.getPromo());
    Group group;
    if(optional.isEmpty()) {
      GroupCreationDTO dto = new GroupCreationDTO();
      dto.setAdmins(Arrays.asList(1L));
      dto.setName("Promo "+student.getPromo());
      dto.setRestricted(true);
      group = createGroup(dto);
    }else group = optional.get();

    GroupMemberDTO memberDTO = new GroupMemberDTO();
    memberDTO.setStudentId(student.getId());
    addMember(group, memberDTO, true);
  }

  public boolean isInPromoGroup(Student student) {
    Optional<Group> optional = groupRepository.findOneByName("Promo "+student.getPromo());
    return optional.isPresent() && groupMemberRepository.isMemberOfGroup(optional.get().getId(), student.getId());
  }

  public void addToNeurchiIfNotPresent(Student student){
    Optional<Group> optionalNeurchi = groupRepository.findById(118826L);

    if(optionalNeurchi.isPresent()){
      GroupMemberDTO groupMemberDTO = new GroupMemberDTO();
      groupMemberDTO.setStudentId(student.getId());

      if(!groupMemberRepository.isMemberOfGroup(optionalNeurchi.get().getId(), student.getId())){
        addMember(optionalNeurchi.get(), groupMemberDTO, true);
      }
    }

  }

}
