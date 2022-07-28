package com.iseplife.api.dao.gallery;

import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.post.embed.Gallery;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryRepository extends CrudRepository<Gallery, Long> {

  Page<Gallery> findAllByFeedAndPseudoIsFalse(Feed feed, Pageable pageable);

  Page<Gallery> findAllByClubOrderByCreationDesc(Club club, Pageable pageable);

  @Query(
    "select e as event, g as gallery " +
      "from Gallery g join g.feed.event e left join e.targets t " +
      "where e.club.id = :clubId and g.pseudo = false " +
      "and (:admin = true or (" +
        "e.publishedAt < CURRENT_TIMESTAMP " +
        "and (e.targets is empty or t.id in :feeds)" +
      ")) order by g.creation desc"
  )
  Page<EventGalleryProjection> findEventsGalleriesFrom(Long clubId, Boolean admin, List<Long> feeds, Pageable p);
}
