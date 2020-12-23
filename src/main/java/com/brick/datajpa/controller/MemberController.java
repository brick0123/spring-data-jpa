package com.brick.datajpa.controller;

import com.brick.datajpa.dto.MemberDto;
import com.brick.datajpa.entity.Member;
import com.brick.datajpa.repository.MemberRepository;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

  private final MemberRepository memberRepository;

/*  @GetMapping("/members")
  public Page<Member> list(Pageable pageable) {
    // 구현체 PageRequest 객체를 생성
    // http://localhost:8080/members?page=0&size=3&sort=id,desc
    return memberRepository.findAll(pageable);
  }*/

  @GetMapping("/members")
  public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) {
    // 구현체 PageRequest 객체를 생성
    // http://localhost:8080/members?page=0&size=3&sort=id,desc
    return memberRepository.findAll(pageable)
        .map(MemberDto::new);
  }

  @PostConstruct
  public void init() {
    for (int i = 0; i < 100; i++) {
      memberRepository.save(new Member("user" + i, i));
    }
  }

}
