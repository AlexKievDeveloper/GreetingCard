package com.greetingcard.service.impl;

import com.greetingcard.dao.jdbc.FlywayConfig;
import com.greetingcard.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

@SpringJUnitWebConfig(value = FlywayConfig.class)
class DefaultEmailServiceITest {
    @Autowired
    EmailService service;

    @Test
    void sendMail() {
        service.sendMail("nomarchia2@gmail.com","Test Subject","https://www.youtube.com/?gl=UA&tab=r1");
    }


}