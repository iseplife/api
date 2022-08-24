package com.iseplife.api.dao.rich;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iseplife.api.entity.post.embed.rich.RichLink;

@Repository
public interface RichLinkRepository extends CrudRepository<RichLink, Long> {

}
