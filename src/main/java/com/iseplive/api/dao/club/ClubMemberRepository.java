package com.iseplive.api.dao.club;

import com.iseplive.api.entity.club.ClubMember;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Guillaume on 30/07/2017.
 * back
 */
@Repository
public interface ClubMemberRepository extends CrudRepository<ClubMember, Long> {
  List<ClubMember> findByClubId(Long club_id);

  ClubMember findOneByStudentIdAndClubId(Long student_id, Long club_id);

  List<ClubMember> findByStudentId(Long student_id);
}
