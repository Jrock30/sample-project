package com.sample.project.api.login.controller;

import com.sample.project.projectFoApiApplication;
import com.sample.project.api.login.dto.MemberListDto;
import com.sample.project.api.login.dto.RequestSearchMemberListDto;
import com.sample.project.api.login.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = projectFoApiApplication.class)
class MemberControllerTest {

    @Autowired
    private MemberService memberService;

    @DisplayName("회원관리 목록 조회")
    @Test
    void searchMemberList() {

        // given
        RequestSearchMemberListDto requestSearchMemberListDto = new RequestSearchMemberListDto();
        // page num, 페이지에 보여줄 row 수,
//        Pageable pageable = PageRequest.of(1, 2, Sort.by(Sort.Direction.DESC, "userId"));
        Pageable pageable = PageRequest.of(1, 2);

        // when
        Page<MemberListDto> temp = memberService.searchMemberList(requestSearchMemberListDto, pageable);

        // then
        assertAll(
                () -> assertTrue(temp.getSize()>0)
        );

    }
}