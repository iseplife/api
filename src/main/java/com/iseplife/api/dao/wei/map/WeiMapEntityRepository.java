package com.iseplife.api.dao.wei.map;

import com.iseplife.api.entity.wei.map.WeiMapEntity;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeiMapEntityRepository extends CrudRepository<WeiMapEntity, Integer> {
  @Query("select e from WeiMapEntity e where e.enabled = true")
  List<WeiMapEntity> findAllEnabled();
}
