package com.iseplife.api;

import com.google.common.collect.Sets;
import com.iseplife.api.constants.FeedConstant;
import com.iseplife.api.dao.feed.FeedRepository;
import com.iseplife.api.dao.student.RoleRepository;
import com.iseplife.api.dao.student.StudentRepository;
import com.iseplife.api.entity.Feed;
import com.iseplife.api.entity.user.Role;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.constants.Roles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    /* Create main feeds */
    List<Feed> feeds = new ArrayList<>();
    List<String> savedFeeds = feedRepository.findAll().stream().map(Feed::getName).collect(Collectors.toList());
    for (FeedConstant f : FeedConstant.values()) {
      if(!savedFeeds.contains(f.name())){
        Feed feed = new Feed();
        feed.setName(f.name());
        feeds.add(feed);
      }
    }
    feedRepository.saveAll(feeds);
  }
}
