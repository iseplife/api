package com.iseplife.api.dao.gallery;

import com.iseplife.api.entity.feed.Feed;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.post.embed.Gallery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryRepository extends CrudRepository<Gallery, Long> {

  Page<Gallery> findAllByFeedAndPseudoIsFalse(Feed feed, Pageable pageable);

  Page<Gallery> findAllByClubOrderByCreationDesc(Club club, Pageable pageable);
}
