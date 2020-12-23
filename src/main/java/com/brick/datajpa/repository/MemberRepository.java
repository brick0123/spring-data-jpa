package com.brick.datajpa.repository;

import com.brick.datajpa.entity.Member;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

  List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

  List<Member> findTop3HelloBy();

  @Query("select m from Member m where m.username = :username and  m.age = :age")
  List<Member> findMember(@Param("username") String username, @Param("age") int age);

//    @Query("select new com.brick.datajpa.dto.MemberDto(m.id, m.username, m.team) from Member m join m.team")
//    List<MemberDto> findMemberDto();

  @Query("select m from Member m where m.username in :names")
  List<Member> findByNames(@Param("names") Collection<String> names);

  List<Member> findListByUsername(String username);

  Member findMemberByUsername(String username);

  Optional<Member> findOptionalByUsername(String username);

  @Query(value = "select m from Member m left join m.team t",
      countQuery = "select count(m) from Member m")
//  @Query("select m from Member m left join m.team t")
  Page<Member> findByAge(int age, Pageable pageable);

  //  @Modifying // executeUpdate
  @Modifying(clearAutomatically = true) // em.clear 자동으로 해줌
  @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
  int bulkAgePlus(@Param("age") int age);

  @Override
  @EntityGraph(attributePaths = {"team"})
  List<Member> findAll();


  @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
  Member findReadOnlyByUsername(String username);
}
