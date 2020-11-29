package com.brick.datajpa.dto;

import com.brick.datajpa.entity.Team;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberDto {

  private Long id;
  private String userName;
  private Team teamName;
}
