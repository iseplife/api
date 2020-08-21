package com.iseplife.api.dao.gallery;

import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.post.embed.Gallery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GalleryRepository extends CrudRepository<Gallery, Long> {

  List<Gallery> findAllByFeedAndPseudoIsFalse(Feed feed);


  @Query("select g from Gallery g, Event e " +
    "where g.feed = e.feed " +
    "and e.club = :club"
  )
  Page<Gallery> findAllByClub(@Param("club") Club club, Pageable pageable);
}
