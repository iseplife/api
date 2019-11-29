package com.iseplife.api.dao.post;

import com.iseplife.api.entity.media.Embed;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmbedRepository  extends CrudRepository<Embed, Long> {
}
