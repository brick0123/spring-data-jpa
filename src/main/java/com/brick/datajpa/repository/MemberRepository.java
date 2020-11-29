package com.brick.datajpa.repository;

import com.brick.datajpa.dto.MemberDto;
import com.brick.datajpa.entity.Member;
import java.util.Collection;
import java.util.Collections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3HelloBy();

    @Query("select m from Member m where m.username = :username and  m.age = :age")
    List<Member> findMember(@Param("username") String username, @Param("age") int age);

    @Query("select new com.brick.datajpa.dto.MemberDto(m.id, m.username, m.team) from Member m join m.team")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);
}
