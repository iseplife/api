package com.iseplife.api.dao.event;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iseplife.api.entity.event.EventPosition;

@Repository
public interface EventPositionRepository extends CrudRepository<EventPosition, String> { }