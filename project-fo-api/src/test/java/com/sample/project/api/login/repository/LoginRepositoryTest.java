package com.sample.project.api.login.repository;

import com.sample.project.api.login.entity.LoginMemberEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
//@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
class LoginRepositoryTest {

    @Autowired
    private LoginRepository loginRepository;

    @DisplayName("로그인 테스트")
    @Test
    void searchMember() {
        String userId = "jrock";
        Optional<LoginMemberEntity> loginMemberEntity = loginRepository.searchMember(userId);

        assertThat(loginMemberEntity).isNotEmpty();
    }

}