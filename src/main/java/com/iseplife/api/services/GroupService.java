package com.iseplife.api.services;

import com.cloudinary.utils.ObjectUtils;
import com.iseplife.api.constants.GroupType;
import com.iseplife.api.dao.group.GroupFactory;
import com.iseplife.api.dao.group.GroupRepository;
import com.iseplife.api.dto.group.groupDTO;
import com.iseplife.api.dto.group.view.GroupView;
import com.iseplife.api.entity.Group;
import com.iseplife.api.exceptions.IllegalArgumentException;
import com.iseplife.api.services.fileHandler.FileHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

@Service
public class GroupService {

  @Autowired
  GroupRepository groupRepository;

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

  public Page<GroupView> getAll(int page) {
    return groupRepository
      .findAll(PageRequest.of(page, RESULTS_PER_PAGE))
      .map(GroupFactory::toView);
  }

  public GroupView createGroup(groupDTO dto, MultipartFile file) {
    Group group = GroupFactory.fromDTO(dto);

    if (file != null){
      Map params = ObjectUtils.asMap(
        "process", "compress",
        "size", "200x200"
      );
      group.setCover(fileHandler.upload(file, "", false, params));
    }


    group.setAdmins(new HashSet<>(studentService.getStudents(dto.getAdmins())));

    return GroupFactory.toView(groupRepository.save(group));
  }

  public GroupView updateGroup(Long id, groupDTO dto, MultipartFile file) {
    Group group = getGroup(id);
    GroupFactory.updateFromDTO(group, dto);
    group.setAdmins(new HashSet<>(studentService.getStudents(dto.getAdmins())));

    if (file != null) {
      if(group.getCover() != null)
        fileHandler.delete(group.getCover());

      Map params = ObjectUtils.asMap(
        "process", "compress",
        "size", "200x200"
      );
      group.setCover(fileHandler.upload(file, "", false, params));
    } else if (dto.getResetCover()) {
      fileHandler.delete(group.getCover());
    }
    group.setAdmins(new HashSet<>(studentService.getStudents(dto.getAdmins())));

    return GroupFactory.toView(groupRepository.save(group));
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
