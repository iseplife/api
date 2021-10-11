package com.iseplife.api.services;

import com.iseplife.api.conf.StorageConfig;
import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.GroupType;
import com.iseplife.api.dao.group.GroupFactory;
import com.iseplife.api.dao.group.GroupMemberFactory;
import com.iseplife.api.dao.group.GroupMemberRepository;
import com.iseplife.api.dao.group.GroupRepository;
import com.iseplife.api.dto.group.GroupCreationDTO;
import com.iseplife.api.dto.group.GroupMemberDTO;
import com.iseplife.api.dto.group.GroupUpdateDTO;
import com.iseplife.api.dto.group.view.GroupAdminView;
import com.iseplife.api.dto.group.view.GroupMemberView;
import com.iseplife.api.dto.group.view.GroupPreview;
import com.iseplife.api.dto.group.view.GroupView;
import com.iseplife.api.entity.group.Group;
import com.iseplife.api.entity.GroupMember;
import com.iseplife.api.exceptions.HttpForbiddenException;
import com.iseplife.api.exceptions.HttpBadRequestException;
import com.iseplife.api.exceptions.HttpNotFoundException;
import com.iseplife.api.services.fileHandler.FileHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupService {

  @Autowired
  GroupRepository groupRepository;

  @Autowired
  GroupMemberRepository groupMemberRepository;

  @Qualifier("FileHandlerBean")
  @Autowired
  FileHandler fileHandler;

  @Autowired
  private StudentService studentService;

  @Autowired
  private FeedService feedService;


  private static final int RESULTS_PER_PAGE = 20;

  public Group getGroup(Long id) {
    Optional<Group> group = groupRepository.findById(id);
    if (group.isEmpty() || (group.get().isRestricted() && !groupMemberRepository.isMemberOfGroup(id, SecurityService.getLoggedId())))
      throw new HttpNotFoundException("gallery_not_found");

    return group.get();
  }

  public List<GroupMemberView> getGroupMembers(Long id) {
    return groupMemberRepository.findByGroup_Id(id)
      .stream()
      .map(GroupMemberFactory::toView)
      .collect(Collectors.toList());
  }

  public GroupMember getGroupMember(Long id) {
    Optional<GroupMember> member = groupMemberRepository.findById(id);
    if (member.isEmpty())
      throw new HttpNotFoundException("member_not_found");

    return member.get();
  }

  public GroupView getGroupView(Long id) {
    Group group = getGroup(id);
    return GroupFactory.toView(group, feedService.isSubscribedToFeed(group));
  }

  public GroupAdminView getGroupAdmin(Long id) {
    return GroupFactory.toAdminView(getGroup(id));
  }


  public Page<GroupPreview> getAll(int page) {
    return groupRepository
      .findAll(PageRequest.of(page, RESULTS_PER_PAGE))
      .map(GroupFactory::toPreview);
  }

  public List<GroupPreview> getUserGroups(TokenPayload token) {
    return groupRepository
      .findAllUserGroups(token.getId())
      .stream()
      .map(GroupFactory::toPreview)
      .collect(Collectors.toList());
  }


  public GroupAdminView createGroup(GroupCreationDTO dto) {
    Group group = GroupFactory.fromDTO(dto);

    group.setMembers(createGroupAdminMembers(dto.getAdmins()));

    return GroupFactory.toAdminView(groupRepository.save(group));
  }

  private List<GroupMember> createGroupAdminMembers(List<Long> ids) {
    List<GroupMember> adminMembers = new ArrayList<>();
    studentService.getStudents(ids).forEach(a -> {
      GroupMember member = new GroupMember();
      member.setAdmin(true);
      member.setStudent(a);

      adminMembers.add(member);
    });

    return adminMembers;
  }


  public GroupAdminView updateGroup(Long id, GroupUpdateDTO dto) {
    Group group = getGroup(id);
    GroupFactory.updateFromDTO(group, dto);

    // Keep only admins that are in dto.admins
    group.setMembers(
      group.getMembers().stream()
        .filter(m -> {
          if (dto.getAdmins().contains(m.getId())) {
            // In case the member wasn't already an admin
            m.setAdmin(true);

            dto.getAdmins().remove(m.getId());
            return true;
          } else return !m.isAdmin();
        })
        .collect(Collectors.toList())
    );

    // We create a group member admin for all leftovers ids
    group.getMembers().addAll(createGroupAdminMembers(dto.getAdmins()));

    return GroupFactory.toAdminView(groupRepository.save(group));
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

  public GroupMemberView addMember(Long id, GroupMemberDTO dto) {
    Group group = getGroup(id);
    if (!SecurityService.hasRightOn(group))
      throw new HttpForbiddenException("insufficient_rights");

    GroupMember member = new GroupMember();
    member.setAdmin(false);
    member.setStudent(studentService.getStudent(dto.getStudentId()));
    member.setGroup(group);

    return GroupMemberFactory.toView(groupMemberRepository.save(member));
  }

  public Boolean removeMember(Long id, Long member) {
    GroupMember groupMember = getGroupMember(member);
    if (!SecurityService.hasRightOn(groupMember.getGroup()))
      throw new HttpForbiddenException("insufficient_rights");

    if (groupMember.isAdmin() && groupMemberRepository.findGroupAdminCount(groupMember.getGroup()) < 1)
      throw new HttpBadRequestException("minimum_admins_size_required");

    groupMemberRepository.delete(groupMember);
    return true;
  }

}
