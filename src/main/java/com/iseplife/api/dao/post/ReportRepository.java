package com.iseplife.api.dao.post;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iseplife.api.dao.post.projection.ReportProjection;
import com.iseplife.api.entity.post.Comment;
import com.iseplife.api.entity.post.Post;
import com.iseplife.api.entity.post.Report;
import com.iseplife.api.entity.user.Student;

@Repository
public interface ReportRepository extends CrudRepository<Report, Long> {
  public Boolean existsByCommentAndStudent(Comment comment, Student student);
  public Boolean existsByPostAndStudent(Post post, Student student);
  
  @Query("select report from Report report")
  public List<ReportProjection> getAllReports();
}
