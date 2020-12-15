package com.brick.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.brick.datajpa.entity.Member;
import com.brick.datajpa.entity.Team;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  TeamRepository teamRepository;

  @PersistenceContext
  EntityManager em;

  @Test
  void testMember() {
    Member member = new Member("memberA");
    Member savedMember = memberRepository.save(member);

    Member findMember = memberRepository.findById(savedMember.getId()).get();

    assertThat(findMember.getId()).isEqualTo(member.getId());
    assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    assertThat(findMember).isSameAs(member);
  }

  @Test
  void basicCRUD() {
    Member member1 = new Member("member1");
    Member member2 = new Member("member2");
    memberRepository.save(member1);
    memberRepository.save(member2);

    // 단건 조회 검증
    Member findMember1 = memberRepository.findById(member1.getId()).get();
    Member findMember2 = memberRepository.findById(member2.getId()).get();
    assertThat(findMember1).isSameAs(member1);
    assertThat(findMember2).isSameAs(member2);

    // 리스트 조회 검증
    List<Member> findAll = memberRepository.findAll();
    assertThat(findAll.size()).isEqualTo(2);

    // 카운트 검증
    long count = memberRepository.count();
    assertThat(count).isEqualTo(2);

    // 삭제 검증
    memberRepository.delete(member1);
    memberRepository.delete(member2);

    long deletedCount = memberRepository.count();
    assertThat(deletedCount).isEqualTo(0);
  }


  @Test
  void findByUsernameAndAgeGraterThen() {
    // given
    Member member1 = new Member("aa", 15);
    Member member2 = new Member("aa", 20);

    memberRepository.save(member1);
    memberRepository.save(member2);
    // when

    List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("aa", 15);

    // then
    assertThat(result.get(0).getUsername()).isEqualTo("aa");
    assertThat(result.get(0).getAge()).isEqualTo(20);
  }

  @Test
  void testQuery() {
    Member member1 = new Member("aa", 15);
    Member member2 = new Member("aa", 20);

    memberRepository.save(member1);
    memberRepository.save(member2);

    List<Member> result = memberRepository.findMember("aa", 15);
    assertThat(result.get(0)).isEqualTo(member1);
  }

  @Test
  void findMemberDto() {
    Team team = new Team();
    teamRepository.save(team);

    Member member = new Member("AAA", 10);
    member.changeTeam(team);
    memberRepository.save(member);

//    List<MemberDto> memberDto = memberRepository.findMemberDto();
//    for (MemberDto dto : memberDto) {
//      System.out.println("dto = " + dto);
//    }
  }

  @Test
  void finByNames() {

    Member member1 = new Member("AAA", 10);
    Member member2 = new Member("BBB", 10);

    memberRepository.save(member1);
    memberRepository.save(member2);

    List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

    for (Member member : result) {
      System.out.println("member = " + member);
    }
  }

  @Test
  void returnType() {

    Member member1 = new Member("AAA", 10);
    Member member2 = new Member("BBB", 10);

    memberRepository.save(member1);
    memberRepository.save(member2);

//    List<Member> member = memberRepository.findListByUsername("AAA");
//    Member findMember = memberRepository.findMemberByUsername("AAA");
//    System.out.println("findMember = " + findMember);

  }

  @Test
  void paging() {
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 10));
    memberRepository.save(new Member("member3", 10));
    memberRepository.save(new Member("member4", 10));
    memberRepository.save(new Member("member5", 10));

    int age = 10;

    PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Direction.DESC, "username"));

    // when
    Page<Member> page = memberRepository.findByAge(age, pageRequest);
//
//    Page<MemberDto> dto = memberRepository.findByAge(age, pageRequest)
//        .map(member -> new MemberDto(member.getId(), member.getUsername(), null));

    // then
    List<Member> content = page.getContent();
    long totalElements = page.getTotalElements();

    for (Member member : content) {
      System.out.println("member = " + member);
    }

    System.out.println("totalElements = " + totalElements);

    assertThat(content.size()).isEqualTo(3);
    assertThat(page.getTotalElements()).isEqualTo(5);
    assertThat(page.getNumber()).isEqualTo(0);
    assertThat(page.getTotalPages()).isEqualTo(2);
    assertThat(page.isFirst()).isTrue();
    assertThat(page.hasNext()).isTrue();
  }

  @Test
  void bulkUpdate() {
    // given
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 14));
    memberRepository.save(new Member("member3", 20));
    memberRepository.save(new Member("member4", 21));
    memberRepository.save(new Member("member5", 22));

    // 영속성 컨텍스트에 있고 아직 db 반영이 안 됨.

    // when
    int resultCount = memberRepository.bulkAgePlus(20);
//    em.clear();

    Member member5 = memberRepository.findMemberByUsername("member5");
    System.out.println("member5 = " + member5);

    // then
    assertThat(resultCount).isEqualTo(3);
  }

  @Test
  void findMemberLazy() {
    // given
    // member1 -> teamA
    // member2 -> teamB

    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    teamRepository.save(teamA);
    teamRepository.save(teamB);

    Member member1 = new Member("member1", 10, teamA);
    Member member2 = new Member("member2", 10, teamB);
    memberRepository.save(member1);
    memberRepository.save(member2);
    
    em.flush();
    em.clear();

    List<Member> members = memberRepository.findAll();

    for (Member member : members) {
      System.out.println("member.getUsername() = " + member.getUsername());
      System.out.println("member.team = " + member.getTeam().getName());
      System.out.println("member = " + member.getUsername());
    }
  }

  @Test
  void queryHint() {
    System.out.println("before");
    Member member1 = new Member("member1", 10);
    memberRepository.save(member1);
    System.out.println("after");
    em.flush();
    em.clear();

//    Member findMember = memberRepository.findById(member1.getId()).get();
    Member findMember = memberRepository.findReadOnlyByUsername("member1");
    findMember.changeName("member2");

    em.flush();
  }

  @Test
  void callCustom() {
    List<Member> result = memberRepository.findMemberCustom();
  }
}