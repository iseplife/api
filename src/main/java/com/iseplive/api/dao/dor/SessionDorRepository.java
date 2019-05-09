package com.iseplive.api.dao.dor;

import com.iseplive.api.entity.dor.SessionDor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Guillaume on 09/02/2018.
 * back
 */
@Repository
public interface SessionDorRepository extends CrudRepository<SessionDor, Long> {
  SessionDor findByEnabled(Boolean enabled);
  List<SessionDor> findAll();
}
