package com.iseplive.api.dao.post;

import com.iseplive.api.entity.user.Author;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Guillaume on 03/08/2017.
 * back
 */
@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {
}
