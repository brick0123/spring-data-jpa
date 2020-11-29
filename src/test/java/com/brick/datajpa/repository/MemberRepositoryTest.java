package com.brick.datajpa.repository;

import com.brick.datajpa.dto.MemberDto;
import com.brick.datajpa.entity.Member;
import com.brick.datajpa.entity.Team;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamJpaRepository teamJpaRepository;

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
        teamJpaRepository.save(team);

        Member member = new Member("AAA", 10);
        member.changeTeam(team);
        memberRepository.save(member);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }


}