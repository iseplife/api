package com.iseplife.api;

import com.iseplife.api.constants.FeedType;
import com.iseplife.api.constants.GroupType;
import com.iseplife.api.dao.group.GroupRepository;
import com.iseplife.api.dao.student.RoleRepository;
import com.iseplife.api.dao.student.StudentRepository;
import com.iseplife.api.dao.wei.room.WeiRoomRepository;
import com.iseplife.api.entity.group.GroupMember;
import com.iseplife.api.entity.group.Group;
import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.user.Role;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.entity.wei.WeiRoom;
import com.iseplife.api.entity.wei.WeiRoomMember;
import com.iseplife.api.services.SubscriptionService;
import com.iseplife.api.constants.Roles;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class DatabaseSeeder {
  final private StudentRepository studentRepository;
  final private RoleRepository roleRepository;
  final private GroupRepository groupRepository;
  final private SubscriptionService subscriptionService;
  final private WeiRoomRepository roomRepository;
  final private Logger LOG = LoggerFactory.getLogger(DatabaseSeeder.class);

  void seedDatabase() {
    
   /* for(WeiRoom room : roomRepository.findAll()) {
      if(room.isBooked())
        for(WeiRoomMember mem : room.getMembers())
          System.out.println(mem.getStudent().getId()+","+mem.getStudent().getFirstName()+","+mem.getStudent().getLastName()+","+room.getRoomId()+","+room.getCapacity());
    }
    */
    List<String> rooms4 = new ArrayList<>();
    List<String> rooms5 = new ArrayList<>();
    List<String> rooms6 = new ArrayList<>();
    List<String> rooms7 = new ArrayList<>();
    List<String> rooms8 = new ArrayList<>();

    rooms4.add("R 117");
    rooms4.add("R 121");
    rooms4.add("O 194");
    
    rooms5.add("J 177");
    rooms5.add("J 178");
    rooms5.add("J 179");
    rooms5.add("J 180");
    rooms5.add("J 181");
    rooms5.add("J 182");
    rooms5.add("J 183");
    rooms5.add("J 184");
    rooms5.add("J 185");
    rooms5.add("J 186");
    rooms5.add("J 187");
    rooms5.add("J 188");
    rooms5.add("J 189");
    rooms5.add("J 190");
    rooms5.add("J 191");
    
    rooms5.add("J 227");
    rooms5.add("J 228");
    rooms5.add("J 229");
    rooms5.add("J 230");
    rooms5.add("J 231");
    
    rooms5.add("J 203");
    rooms5.add("J 204");
    rooms5.add("J 205");
    
    rooms5.add("J 192");
    rooms5.add("J 193");
    
    
    rooms6.add("S 105");
    rooms6.add("S 106");
    rooms6.add("S 107");
    rooms6.add("S 111");
    rooms6.add("S 112");

    rooms6.add("BC 114");
    rooms6.add("BC 116");
    rooms6.add("BC 118");
    rooms6.add("BC 119");
    rooms6.add("BC 122");
    rooms6.add("BC 123");
    rooms6.add("BC 124");
    
    rooms6.add("BC 132");
    rooms6.add("BC 133");
    rooms6.add("BC 134");
    rooms6.add("BC 135");
    rooms6.add("BC 137");
    rooms6.add("BC 139");
    rooms6.add("BC 141");
    rooms6.add("BC 142");
    rooms6.add("BC 144");
    rooms6.add("BC 146");
    rooms6.add("BC 148");
    rooms6.add("BC 149");
    rooms6.add("BC 150");
    rooms6.add("BC 151");
    rooms6.add("BC 152");
    rooms6.add("BC 154");
    rooms6.add("B 165");
    rooms6.add("B 166");
    rooms6.add("B 167");
    rooms6.add("B 168");
    rooms6.add("B 169");
    rooms6.add("B 170");
    rooms6.add("B 171");
    rooms6.add("B 172");
    rooms6.add("B 173");
    rooms6.add("B 174");
    rooms6.add("B 175");
    rooms6.add("B 176");
    
    
    
    rooms7.add("JC 145");
    
    rooms7.add("JC 136");

    rooms7.add("JC 127");
    rooms7.add("JC 128");
    rooms7.add("JC 129");
    rooms7.add("JC 130");
    
    
    
    rooms8.add("SC 115");
    rooms8.add("SC 125");
    rooms8.add("SC 126");
    rooms8.add("SC 131");
    rooms8.add("SC 138");
    rooms8.add("SC 140");
    rooms8.add("SC 147");
    rooms8.add("SC 153");
    rooms8.add("SC 155");
    rooms8.add("SC 156");
    rooms8.add("P 159");
    rooms8.add("P 160");
    rooms8.add("P 161");
    rooms8.add("SC 162");
    rooms8.add("SC 163");
    rooms8.add("P 164");
    rooms8.add("BB 195");
    rooms8.add("BB 196");
    rooms8.add("BB 197");
    rooms8.add("BB 206");
    rooms8.add("BB 207");
    
    


    /* for(String id : rooms4) {
      WeiRoom room = new WeiRoom();
      room.setRoomId(id);
      room.setCapacity(4);
      roomRepository.save(room);
    }
    for(String id : rooms5) {
      WeiRoom room = new WeiRoom();
      room.setRoomId(id);
      room.setCapacity(5);
      roomRepository.save(room);
    }
    for(String id : rooms6) {
      WeiRoom room = new WeiRoom();
      room.setRoomId(id);
      room.setCapacity(6);
      roomRepository.save(room);
    }

    for(String id : rooms7) {
      WeiRoom room = new WeiRoom();
      room.setRoomId(id);
      room.setCapacity(7);
      roomRepository.save(room);
    }

    for(String id : rooms8) {
      WeiRoom room = new WeiRoom();
      room.setRoomId(id);
      room.setCapacity(8);
      roomRepository.save(room);
    }*/
    
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
    return studentRepository.existsById(1L);
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
    student.setLastName("ZIDANE");
    student.setPromo(1998);
    student.setBirthDate(new Date());

    student.setFeed(new Feed(student.getName(), FeedType.STUDENT));

    Role roleStudent = roleRepository.findByRole(Roles.STUDENT);
    Role roleAdmin = roleRepository.findByRole(Roles.ADMIN);
    student.setRoles(Set.of(roleAdmin, roleStudent));

    studentRepository.save(student);

    subscriptionService.subscribe(student, false);

    /* Create default group and add super admin as member */
    List<Group> groups = new ArrayList<>();
    List<GroupType> existingTypes = groupRepository
      .findDistinctType()
      .stream()
      .map(Group::getType)
      .collect(Collectors.toList());

    if(existingTypes.size() != 4) {
      for (GroupType type: GroupType.values()){
        if(!existingTypes.contains(type) && type != GroupType.DEFAULT){
          Group g = new Group();
          g.setName(type.getName());
          g.setFeed(new Feed(type.getName(), FeedType.GROUP));
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
