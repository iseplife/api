package com.iseplife.api.services;

import com.cloudinary.utils.ObjectUtils;
import com.iseplife.api.conf.StorageConfig;
import com.iseplife.api.conf.jwt.TokenPayload;
import com.iseplife.api.constants.GroupType;
import com.iseplife.api.dao.group.GroupFactory;
import com.iseplife.api.dao.group.GroupMemberRepository;
import com.iseplife.api.dao.group.GroupRepository;
import com.iseplife.api.dto.group.GroupDTO;
import com.iseplife.api.dto.group.view.GroupPreview;
import com.iseplife.api.dto.group.view.GroupView;
import com.iseplife.api.entity.group.Group;
import com.iseplife.api.entity.GroupMember;
import com.iseplife.api.exceptions.AuthException;
import com.iseplife.api.exceptions.IllegalArgumentException;
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


  private static final int RESULTS_PER_PAGE = 20;

  public Group getGroup(Long id) {
    Optional<Group> group = groupRepository.findById(id);
    if (group.isEmpty())
      throw new IllegalArgumentException("could not find the group with id: " + id);

    return group.get();
  }

  public GroupView getGroupView(Long id) {
    Group group = getGroup(id);
    if(groupMemberRepository.isMemberOfGroup(id, AuthService.getLoggedId()))
      return GroupFactory.toView(group);

    throw new IllegalArgumentException("could not find the group with id: " + id);
  }


  public Page<GroupView> getAll(int page) {
    return groupRepository
      .findAll(PageRequest.of(page, RESULTS_PER_PAGE))
      .map(GroupFactory::toView);
  }

  public List<GroupPreview> getUserGroups(TokenPayload token) {
    return groupRepository
      .findAllUserGroups(token.getId())
      .stream()
      .map(GroupFactory::toPreview)
      .collect(Collectors.toList());
  }

  public GroupView createGroup(GroupDTO dto, MultipartFile file) {
    Group group = GroupFactory.fromDTO(dto);

    if (file != null){
      Map params = ObjectUtils.asMap(
        "process", "compress",
        "sizes", StorageConfig.COVER_SIZES
      );
      group.setCover(fileHandler.upload(file, StorageConfig.PATH.get("feed_cover"), false, params));
    }

    studentService.getStudents(dto.getAdmins()).forEach(a -> {
      GroupMember member = new GroupMember();
      member.setAdmin(true);
      member.setStudent(a);
      group.getMembers().add(member);
    });


    return GroupFactory.toView(groupRepository.save(group));
  }


  public GroupView updateGroup(Long id, GroupDTO dto) {
    Group group = getGroup(id);
    GroupFactory.updateFromDTO(group, dto);

    studentService.getStudents(dto.getAdmins()).forEach(a -> {
      GroupMember member = new GroupMember();
      member.setAdmin(true);
      member.setStudent(a);
      group.getMembers().add(member);
    });
    return GroupFactory.toView(groupRepository.save(group));
  }

  public String updateCover(Long id, MultipartFile cover){
    Group group = getGroup(id);
    if (!AuthService.hasRightOn(group))
      throw new AuthException("You have not sufficient rights on this group (id:" + id + ")");

    if (group.getCover() != null)
      fileHandler.delete(group.getCover());

    if (cover == null) {
      group.setCover(null);
    }else {
      Map params = ObjectUtils.asMap(
        "process", "compress",
        "sizes", StorageConfig.COVER_SIZES
      );
      group.setCover(fileHandler.upload(cover, StorageConfig.PATH.get("feed_cover"), false, params));
    }

    groupRepository.save(group);
    return group.getCover();
  }

  public Boolean toggleArchive(Long id) {
    Group group = getGroup(id);
    if (group.getType() != GroupType.DEFAULT)
      throw new IllegalArgumentException("This type of group cannot be archive");


    group.setArchivedAt(group.isArchived() ? null : new Date());
    groupRepository.save(group);

    return group.isArchived();
  }

  public void deleteGroup(Long id) {
    Group group = getGroup(id);
    if (group.getType() != GroupType.DEFAULT) {
      throw new IllegalArgumentException("This type of group cannot be delete");
    }

    groupRepository.delete(group);
  }

}
