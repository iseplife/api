package com.iseplife.api.dao.club;

import com.iseplife.api.dao.club.projection.ClubMemberProjection;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.club.ClubMember;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.constants.ClubRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubMemberRepository extends CrudRepository<ClubMember, Long> {
  List<ClubMemberProjection> findByClubId(Long club_id);

  Boolean existsByClubIdAndStudentIdAndFromYear(Long club, Long student, Integer Year);

  List<ClubMemberProjection> findByClubIdAndFromYearAndToYear(Long club_id, Integer from, Integer to);

  List<ClubMemberProjection> findByClubIdAndRole(Long club_id, ClubRole role);

  ClubMember findOneByStudentIdAndClubId(Long student_id, Long club_id);

  List<ClubMember> findByStudentId(Long student_id);

  @Query("select count(cm.id) from ClubMember cm " +
    "where cm.club = ?1 " +
    "and cm.role = 'ADMIN' "
  )
  Integer findClubAdminCount(Club club);

  @Query("select cm.student from ClubMember cm " +
    "where cm.club.id = :#{#club.id} " +
    "and cm.role in :#{#role.getParents()}"
  )
  List<Student> findClubPublishers(Club club, ClubRole role);

}
