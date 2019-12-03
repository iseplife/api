package com.iseplife.api.dao;

import com.iseplife.api.entity.post.embed.Gallery;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryRepository extends CrudRepository<Gallery, Long> {
}
