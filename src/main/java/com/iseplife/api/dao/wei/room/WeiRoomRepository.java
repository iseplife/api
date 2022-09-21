package com.iseplife.api.dao.wei.room;

import com.iseplife.api.dao.wei.room.projection.WeiAvailableRoomProjection;
import com.iseplife.api.dao.wei.room.projection.WeiRoomProjection;
import com.iseplife.api.entity.wei.WeiRoom;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeiRoomRepository extends CrudRepository<WeiRoom, String> {
  
  @Query("select r from WeiRoom r where r.id = :id")
  WeiRoomProjection findProjectionById(String id);
  
  @Query("select r from WeiRoom r where r.booked = false and r.capacity = :capacity and r.reservedUpTo <= now()")
  Page<WeiRoom> findFreeOfSize(int capacity, Pageable pageable);
  
  @Query("select count(r) as count, r.capacity as capacity from WeiRoom r where r.reservedUpTo <= now() and r.booked = false group by r.capacity")
  List<WeiAvailableRoomProjection> getAvailableRooms();
  
  @Query("select r from WeiRoom r where r.id = :id")
  Optional<WeiRoom> findById(String id);
}
