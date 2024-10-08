package com.iseplife.api.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import com.iseplife.api.dao.wei.room.WeiRoomMemberRepository;
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
  final private WeiRoomMemberRepository memberRepository;
  final private StudentService studentService;

  private List<WeiMapEntity> lastEntities;
  private long lastEntitiesChecked;
  
  private static Map<Integer, Double[]> map = new HashMap<>();
  private static Map<Long, Integer> studentMap = new HashMap<>();
  static {
    map.put(185, new Double[]{45.110899493450894,1.9594944011047606});
    map.put(186, new Double[]{45.11079397535516,1.9593278784282158});
    map.put(187, new Double[]{45.11074121630728,1.9594573960655286});
    map.put(188, new Double[]{45.11087311392696,1.9596794263009212});
    map.put(189, new Double[]{45.11084673440302,1.9597534363793856});
    map.put(190, new Double[]{45.110714836783345,1.9595684111832246});
    map.put(192, new Double[]{45.11054336987777,1.9600494766932428});
    map.put(193, new Double[]{45.11054336987777,1.9601789943305552});
    map.put(215, new Double[]{45.11034552344825,1.961437165664448});
    map.put(216, new Double[]{45.11034552344825,1.9613076480271354});
    map.put(217, new Double[]{45.11033233368629,1.961104120311359});
    map.put(218, new Double[]{45.11034552344825,1.9610116077132786});
    map.put(219, new Double[]{45.11038509273416,1.9608820900759658});
    map.put(220, new Double[]{45.11038509273416,1.9607340699190374});
    map.put(221, new Double[]{45.11038509273416,1.960604552281725});
    map.put(222, new Double[]{45.11058293916368,1.9602345018894034});
    map.put(223, new Double[]{45.11060931868761,1.9601419892913228});
    map.put(224, new Double[]{45.11062250844958,1.9600679792128588});
    map.put(226, new Double[]{45.11076759583122,1.9599754666147786});
    map.put(227, new Double[]{45.11072802654532,1.9600864817324746});
    map.put(228, new Double[]{45.11067526749744,1.9601974968501712});
    map.put(230, new Double[]{45.11051699035383,1.9607155673994212});
    map.put(231, new Double[]{45.11051699035383,1.9607895774778856});
    map.put(232, new Double[]{45.110503800591864,1.9609375976348142});
    map.put(233, new Double[]{45.110503800591864,1.9610671152721266});
    map.put(234, new Double[]{45.1104906108299,1.9612151354290552});
    map.put(235, new Double[]{45.11047742106793,1.961418663144832});
    map.put(261, new Double[]{45.11046423130596,1.9625843218806447});
    map.put(262, new Double[]{45.11046423130596,1.9624177992040999});
    map.put(263, new Double[]{45.11047742106793,1.9622697790471717});
    map.put(264, new Double[]{45.1104906108299,1.962177266449091});
    map.put(265, new Double[]{45.1104906108299,1.9620477488117785});
    map.put(266, new Double[]{45.1105301801158,1.96189972865485});
    map.put(267, new Double[]{45.11054336987777,1.9617517084979217});
    map.put(268, new Double[]{45.110569749401705,1.9616036883409929});
    map.put(269, new Double[]{45.110569749401705,1.9613446530663678});
    map.put(270, new Double[]{45.11058293916368,1.9611596278702073});
    map.put(271, new Double[]{45.110688457259414,1.9608635875563498});
    map.put(273, new Double[]{45.110714836783345,1.9605120396836448});
    map.put(274, new Double[]{45.110754406069255,1.9604010245659482});
    map.put(275, new Double[]{45.11078078559319,1.9602900094482516});
    map.put(276, new Double[]{45.11080716511712,1.9601789943305552});
    map.put(277, new Double[]{45.11084673440302,1.9600494766932428});
  }
  
  @GetMapping("/entities")
  @RolesAllowed({ Roles.STUDENT })
  public List<WeiMapEntity> getEntities() {
    List<WeiMapEntity> entities = new ArrayList<>(lastEntitiesChecked + 5000 > System.currentTimeMillis() ? lastEntities : weiMapEntityRepository.findAllEnabled());
    if(lastEntitiesChecked + 5000 < System.currentTimeMillis())
      lastEntitiesChecked = System.currentTimeMillis();
    
    var room = memberRepository.findByStudentId(SecurityService.getLoggedId());
    if(room.isPresent()) {
      var roomId = Integer.valueOf(room.get().getRoom().getRoomId());
      Double[] latlong = map.get(roomId);
      WeiMapEntity entity = new WeiMapEntity();
      entity.setAssetUrl("data:image/png;base64,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");
      entity.setName("Votre bungalow : "+roomId);
      entity.setDescription("Votre bungalow est le numéro "+roomId);
      entity.setLat(latlong[0]);
      entity.setLng(latlong[1]);
      entity.setEnabled(true);
      entity.setSize(50);
      System.out.println("add");
      entities.add(entity);
    }
    
    return entities;
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
    var followed = locationRepository.findFollowed(SecurityService.getLoggedId());
    var room = memberRepository.findByStudentId(SecurityService.getLoggedId());
    if(room.isEmpty()) {
      return followed;
    }
    var roomates = locationRepository.findRoomates(SecurityService.getLoggedId(), room.get().getRoom().getId());
    followed.addAll(roomates);

    //deduplicate by student id
    return followed.stream()
      .collect(Collectors.toMap(loc -> loc.getStudent().getId(), Function.identity(), (a, b) -> a))
      .values().stream()
      .collect(Collectors.toList());
  }

  @GetMapping("/activated")
  @RolesAllowed({ Roles.STUDENT })
  public JSONObject activated() {
    return new JSONObject(Map.of("enabled", false, "snapmap", false, "enabled2024", false, "snapmap2024", false));
  }
  @GetMapping("/background")
  @RolesAllowed({ Roles.STUDENT })
  public JSONObject getBackground() {
    return new JSONObject(Map.of("color", "#99B68C"/*"#99B68C"*/, "assetUrl", "/img/wei/map/bg2024.svg"));
  }

}
