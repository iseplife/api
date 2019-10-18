package com.iseplife.api.dao.config;

import com.iseplife.api.entity.Config;
import com.iseplife.api.entity.Config;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends CrudRepository<Config, Long> {
  Config findByKeyName(String keyName);
}
