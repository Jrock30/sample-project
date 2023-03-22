package com.sample.project.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class CommonUtilsTest {

    @Test
    void maskingUserId2() {

        String email = "user@applecorp.com";

        String maskingEmail = CommonUtils.maskingUserId(email);

        log.debug("maskingEmail >> {}", maskingEmail);

    }

    @Test
    void maskingPhoneNumber() {

        String phoneNumber = "01033670450";

        String maskingPhoneNumber = CommonUtils.maskingPhoneNumber(phoneNumber);

        log.debug("maskingPhoneNumber >> {}", maskingPhoneNumber);

        assertThat(maskingPhoneNumber).isEqualTo("010-****-0450");
    }

    @DisplayName("포맷별 현재날짜 구하기")
    @Test
    void makeFormatToday() {

        String format = "yyyy-MM-dd HH:mm:ss";

        String today = CommonUtils.makeFormatToday(format);

//        log.debug("today >> {}", LocalDateTime.parse(today));
        log.debug("today >> {}", today);
    }

    @DisplayName("free test")
    @Test
    void freeTest() {
//        int i = new Random().nextInt(999999);
//        log.debug("Random Number >>> {}", i);
        for (int i = 0; i < 100; i++) {
//            log.debug("Random Number2 >>> {}", ThreadLocalRandom.current().nextInt(100000));
            log.debug("Random Number2 >>> {}", RandomStringUtils.randomNumeric(6));

        }
    }
}