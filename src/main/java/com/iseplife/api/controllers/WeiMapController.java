package com.iseplife.api.controllers;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.wei.map.WeiMapEntityRepository;
import com.iseplife.api.dao.wei.map.WeiMapStudentLocationRepository;
import com.iseplife.api.dao.wei.map.projection.WeiMapStudentLocationProjection;
import com.iseplife.api.dao.wei.room.projection.WeiAvailableRoomProjection;
import com.iseplife.api.dto.wei.map.WeiMapPositionDTO;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.entity.wei.map.WeiMapEntity;
import com.iseplife.api.entity.wei.map.WeiMapStudentLocation;
import com.iseplife.api.services.SecurityService;
import com.iseplife.api.services.StudentService;

import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;

@RestController
@RequestMapping("/wei/map")
@RequiredArgsConstructor
public class WeiMapController {
  final private WeiMapEntityRepository weiMapEntityRepository;
  final private WeiMapStudentLocationRepository locationRepository; 
  final private StudentService studentService;

  private List<WeiMapEntity> lastEntities;
  private long lastEntitiesChecked;
  
  @GetMapping("/entities")
  @RolesAllowed({ Roles.STUDENT })
  public List<WeiMapEntity> getEntities() {
    if(lastEntitiesChecked + 5000 > System.currentTimeMillis())
      return lastEntities;
    
    lastEntitiesChecked = System.currentTimeMillis();
    return lastEntities = weiMapEntityRepository.findAllEnabled();
  }
  
  @PutMapping("/location")
  public void sendLocation(@RequestBody WeiMapPositionDTO position) {
    Student student = studentService.getStudent(SecurityService.getLoggedId());
    
    WeiMapStudentLocation location = new WeiMapStudentLocation();
    location.setLat(position.getLat());
    location.setLng(position.getLng());
    location.setStudent(student);
    
    Date date = new Date();
    location.setTimestamp(date);
    
    location = locationRepository.save(location);
    locationRepository.anonymiseOtherProjections(location.getId(), student);
  }
  
  @GetMapping("/friends")
  @RolesAllowed({ Roles.STUDENT })
  public List<WeiMapStudentLocationProjection> getFriends() {
    return locationRepository.findFollowed(SecurityService.getLoggedId());
  }

  @GetMapping("/activated")
  @RolesAllowed({ Roles.STUDENT })
  public JSONObject activated() {
    return new JSONObject(Map.of("enabled", true, "snapmap", true));
  }

}
