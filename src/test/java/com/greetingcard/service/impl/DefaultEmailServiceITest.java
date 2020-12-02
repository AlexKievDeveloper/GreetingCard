package com.greetingcard.service.impl;


import com.greetingcard.dao.jdbc.TestConfiguration;
import com.greetingcard.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

@SpringJUnitWebConfig(TestConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultEmailServiceITest {
    @Autowired
    EmailService service;

    @Test
    void sendMail() {
        service.sendMail("nomarchia2@gmail.com","Test Subject","Test message body");
    }
}