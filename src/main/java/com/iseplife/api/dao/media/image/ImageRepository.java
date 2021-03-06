package com.iseplife.api.dao.media.image;

import com.iseplife.api.entity.post.embed.media.Image;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Guillaume on 30/07/2017.
 * back
 */
@Repository
public interface ImageRepository extends CrudRepository<Image, Long> {
  List<Image> findImageByIdIn(List<Long> ids);
}
