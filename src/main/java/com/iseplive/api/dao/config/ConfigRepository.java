package com.iseplive.api.dao.config;

import com.iseplive.api.entity.Config;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends CrudRepository<Config, Long> {
  Config findByKeyName(String keyName);
}
