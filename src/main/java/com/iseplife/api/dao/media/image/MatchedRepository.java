package com.iseplife.api.dao.media.image;

import com.iseplife.api.entity.post.embed.media.Image;
import com.iseplife.api.entity.post.embed.media.Matched;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Guillaume on 02/11/2017.
 * back
 */
@Repository
public interface MatchedRepository extends CrudRepository<Matched, Long> {
  List<Matched> findAllByImage(Image image);
  Page<Matched> findAllByMatchId(Long match_id, Pageable pageable);
}
