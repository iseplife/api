package com.iseplife.api.controllers;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iseplife.api.constants.Roles;
import com.iseplife.api.dao.wei.room.WeiRoomMemberRepository;
import com.iseplife.api.dao.wei.room.WeiRoomRepository;
import com.iseplife.api.dao.wei.room.projection.WeiAvailableRoomProjection;
import com.iseplife.api.dao.wei.room.projection.WeiRoomProjection;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.entity.wei.WeiRoom;
import com.iseplife.api.entity.wei.WeiRoomMember;
import com.iseplife.api.exceptions.http.HttpBadRequestException;
import com.iseplife.api.exceptions.http.HttpNotFoundException;
import com.iseplife.api.services.NotificationService;
import com.iseplife.api.services.SecurityService;
import com.iseplife.api.services.StudentService;

import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;

@RestController
@RequestMapping("/wei/rooms")
@RequiredArgsConstructor
public class WeiController {
  final private WeiRoomRepository weiRoomRepository;
  final private WeiRoomMemberRepository roomMemberRepository;
  final private StudentService studentService;
  final private NotificationService notificationService;
  
  private List<WeiAvailableRoomProjection> lastAvailable;
  private long lastAvailableChecked;
  
  @GetMapping("/available")
  @RolesAllowed({Roles.STUDENT})
  public List<WeiAvailableRoomProjection> getAvailableRooms() {
    if(lastAvailableChecked + 1000 > System.currentTimeMillis())
      return lastAvailable;
    
    lastAvailableChecked = System.currentTimeMillis();
    return lastAvailable = weiRoomRepository.getAvailableRooms();
  }

  @GetMapping("/{id}")
  @RolesAllowed({Roles.STUDENT})
  public WeiRoomProjection getRoom(@PathVariable String id) {
    WeiRoomProjection room = weiRoomRepository.findProjectionById(id);
    if(room == null)
      throw new HttpNotFoundException("not_found");
    return room;
  }

  @GetMapping("/book/{size}")
  @RolesAllowed({Roles.STUDENT})
  public synchronized JSONObject bookRoom(@PathVariable Integer size) {
    Page<WeiRoom> booked = weiRoomRepository.findFreeOfSize(size, PageRequest.of(0, 1, Sort.by(Direction.ASC, "reservedUpTo")));

    Student student = studentService.getStudent(SecurityService.getLoggedId());
    
    if(booked.isEmpty())
      throw new HttpNotFoundException("no_more_room");
    
    if(roomMemberRepository.existsByStudentId(student.getId()))
      throw new HttpBadRequestException("already_in_room");
    
    WeiRoom room = booked.get().findFirst().get();
    if(room.getMembers().size() != 0) {
      roomMemberRepository.deleteByRoom(room);
      //notif de tej'
    }
    
    room.setId(UUID.randomUUID().toString());
    weiRoomRepository.save(room);
    
    WeiRoomMember member = new WeiRoomMember();
    member.setAdmin(true);
    member.setStudent(student);
    member.setRoom(room);
    roomMemberRepository.save(member);
    
    room.setReservedUpTo(new Date(System.currentTimeMillis() + 60_000 * size * 4));
    
    weiRoomRepository.save(room);

    return new JSONObject(Map.of("id", room.getId()));
  }
  @GetMapping("/me")
  @RolesAllowed({Roles.STUDENT})
  public synchronized JSONObject myRoom() {
     Optional<WeiRoomMember> member = roomMemberRepository.findByStudentId(SecurityService.getLoggedId());
     if(member.isEmpty())
       return new JSONObject(Map.of("id", ""));
     return new JSONObject(Map.of("id", member.get().getRoom().getId()));
  }
  @PostMapping("/{id}/join")
  @RolesAllowed({Roles.STUDENT})
  public synchronized WeiRoomProjection joinRoom(@PathVariable String id) {
    Student student = studentService.getStudent(SecurityService.getLoggedId());
    
    if(roomMemberRepository.existsByStudentId(student.getId()))
      throw new HttpBadRequestException("already_in_room");
    
    Optional<WeiRoom> optRoom = weiRoomRepository.findById(id);
    if(optRoom.isEmpty())
      throw new HttpNotFoundException("not_found");
    
    WeiRoom room = optRoom.get();
    if(room.getMembers().size() >= room.getCapacity())
      throw new HttpBadRequestException("room_full");
    
    if(room.isBooked())
      throw new HttpBadRequestException("room_booked");

    WeiRoomMember member = new WeiRoomMember();
    member.setAdmin(false);
    member.setStudent(studentService.getStudent(SecurityService.getLoggedId()));
    member.setRoom(room);
    roomMemberRepository.save(member);
    
    room.getMembers().add(member);
    
    if(room.getMembers().size() >= room.getCapacity()) {
      room.setBooked(true);
      weiRoomRepository.save(room);
    }

    return weiRoomRepository.findProjectionById(id);
    
    //Send notif !
    
    //notificationService.sendTempNotification(notif, room.getMembers().stream().map(member -> member.getStudent()).collect(Collectors.toList()));
    
  }

  @PostMapping("/{id}/delete")
  @RolesAllowed({Roles.STUDENT})
  public synchronized void deleteRoom(@PathVariable String id) {
    Optional<WeiRoom> optRoom = weiRoomRepository.findById(id);
    if(optRoom.isEmpty())
      throw new HttpNotFoundException("not_found");
    
    WeiRoom room = optRoom.get();
    
    if(room.isBooked())
      throw new HttpBadRequestException("room_booked");
    
    Optional<WeiRoomMember> myMember = room.getMembers().stream().filter(mem -> mem.getStudent().getId().equals(SecurityService.getLoggedId())).findFirst();
    if(!myMember.isEmpty()) {
      WeiRoomMember member = myMember.get();
      if(!member.isAdmin())
        throw new HttpBadRequestException("not_admin");
      
      roomMemberRepository.deleteByRoom(room);
      
      room.setBooked(false);
      room.setId(UUID.randomUUID().toString());
      room.getMembers().clear();
      
      weiRoomRepository.save(room);
    }else
      throw new HttpBadRequestException("not_member");
    
    //Send notif !
    
    //notificationService.sendTempNotification(notif, room.getMembers().stream().map(member -> member.getStudent()).collect(Collectors.toList()));
    
  }
}
