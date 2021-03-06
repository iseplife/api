package com.iseplife.api.dao.club;

import com.iseplife.api.entity.club.Club;
import com.iseplife.api.entity.club.ClubMember;
import com.iseplife.api.entity.user.Student;
import com.iseplife.api.constants.ClubRole;
import org.springframework.data.jpa.repository.Query;
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

  @Query("select cm.club from ClubMember cm " +
    "where cm.student.id = :#{#student.id} " +
    "and cm.role in :#{#role.getParents()}"
  )
  List<Club> findByRoleWithInheritance(Student student, ClubRole role);

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
