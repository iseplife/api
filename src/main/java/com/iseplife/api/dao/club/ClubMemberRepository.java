package com.iseplife.api.dao.club;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iseplife.api.constants.ClubRole;
import com.iseplife.api.dao.club.projection.ClubMemberProjection;
import com.iseplife.api.dao.club.projection.ClubMemberStudentProjection;
import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.club.ClubMember;

@Repository
public interface ClubMemberRepository extends CrudRepository<ClubMember, Long> {
  List<ClubMemberProjection> findByClubId(Long club_id);

  Boolean existsByClubIdAndStudentIdAndFromYear(Long club, Long student, Integer Year);

  @Query(
    "select cm from ClubMember cm " +
      "where cm.fromYear <= ?2 and cm.toYear >= ?2 and cm.club.id = ?1"
  )
  List<ClubMemberProjection> findClubYearlyMembers(Long club_id, Integer year);

  List<ClubMemberProjection> findByClubIdAndRole(Long club_id, ClubRole role);

  ClubMember findOneByStudentIdAndClubId(Long student_id, Long club_id);

  @Query(
    "select cm from ClubMember cm " +
      "where cm.student.id = :student_id " +
      "and cm.club.viewable = true "
  )
  List<ClubMemberStudentProjection> findByStudentId(Long student_id);

  @Query(
    "select count(cm.id) from ClubMember cm " +
      "where cm.club = ?1 " +
      "and cm.role = 'ADMIN' " +
      "and cm.fromYear <= ?2 and cm.toYear >= ?2 "
  )
  Integer findClubYearlyAdminCount(Club club, Integer year);
}
