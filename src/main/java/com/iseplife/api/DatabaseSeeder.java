package com.iseplife.api;

import com.google.common.collect.Sets;
import com.iseplife.api.constants.GroupType;
import com.iseplife.api.dao.feed.FeedRepository;
import com.iseplife.api.dao.group.GroupRepository;
import com.iseplife.api.dao.student.RoleRepository;
import com.iseplife.api.dao.student.StudentRepository;
import com.iseplife.api.entity.GroupMember;
import com.iseplife.api.entity.group.Group;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.user.Role;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.constants.Roles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
class DatabaseSeeder {

  private final Logger LOG = LoggerFactory.getLogger(DatabaseSeeder.class);

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private FeedRepository feedRepository;

  @Autowired
  private GroupRepository groupRepository;

  void seedDatabase() {
    if (isDatabaseSeeded()) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Database is already seeded. Exiting seeder.");
      }
      return;
    }

    runSeedDatabase();
    if (LOG.isDebugEnabled()) {
      LOG.debug("Database successfully initialized ! Exiting seeder \uD83D\uDC4D");
    }
  }

  private boolean isDatabaseSeeded() {
    return studentRepository.findById(1L).isPresent();
  }

  private void runSeedDatabase() {
    /* Create main/homepage feed */
    Feed mainFeed = new Feed("Homepage");
    mainFeed.setId(1L);

    feedRepository.save(mainFeed);

    /* Create all roles inside Roles class */
    List<Role> roles = new ArrayList<>();
    List<Role> savedRoles = roleRepository.findAll();
    for (Field f : Roles.class.getFields()) {
      try {
        Role role = new Role((String) f.get(Roles.class));
        if (!savedRoles.contains(role)) {
          roles.add(role);
        }
      } catch (Exception e) {
        LOG.debug(e.getMessage());
      }
    }
    roleRepository.saveAll(roles);

    /* Create super admin user */
    Student student = new Student();
    student.setId(1L);
    student.setFirstName("Zinedine");
    student.setLastName("Zidane");
    student.setPromo(1998);
    student.setBirthDate(new Date());

    Role roleStudent = roleRepository.findByRole(Roles.STUDENT);
    Role roleAdmin = roleRepository.findByRole(Roles.ADMIN);
    student.setRoles(Sets.newHashSet(roleStudent, roleAdmin));

    studentRepository.save(student);

    /* Create default group and add super admin as member */
    List<Group> groups = new ArrayList<>();
    List<GroupType> existingTypes = groupRepository
      .findDistinctType()
      .stream()
      .map(Group::getType)
      .collect(Collectors.toList());

    if(existingTypes.size() != 4)
    {
      for (GroupType type: GroupType.values()){
        if(!existingTypes.contains(type) && type != GroupType.DEFAULT){
          Group g = new Group();
          g.setName(type.getName());
          g.setFeed(new Feed(type.getName()));
          g.setRestricted(true);
          g.setType(type);

          GroupMember gm = new GroupMember();
          gm.setStudent(student);
          gm.setAdmin(true);
          gm.setGroup(g);

          g.setMembers(Collections.singletonList(gm));

          groups.add(g);
        }
      }
    }

    groupRepository.saveAll(groups);
  }
}
