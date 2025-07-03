package com.iseplife.api.dao.media;

import com.iseplife.api.constants.MediaStatus;
import com.iseplife.api.entity.post.embed.media.Media;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Repository
public interface MediaRepository extends CrudRepository<Media, Long> {

  Page<Media> findAllByMediaTypeInOrderByCreationDesc(Collection<String> mediaType, Pageable pageable);

  boolean existsByName(String name);

  @Transactional
  @Modifying
  @Query("update Media m set m.status = ?2 where m.name = ?1")
  void updateStatusByName(String name, MediaStatus status);
}
