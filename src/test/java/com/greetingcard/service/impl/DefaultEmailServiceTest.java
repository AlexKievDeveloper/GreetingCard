package com.greetingcard.service.impl;


import com.greetingcard.RootApplicationContext;
import com.greetingcard.dao.jdbc.TestConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;


import static org.mockito.Mockito.verify;

@SpringJUnitWebConfig(value = {TestConfiguration.class,  RootApplicationContext.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultEmailServiceTest {
    @Autowired
    DefaultEmailService service;
    @MockBean
    JavaMailSender mailSender;


    @Test
    void sendMail() {
        //prepare
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("user@test");
        message.setSubject("Test Subject");
        message.setText("Test message body");
        //when
        service.sendMail("user@test","Test Subject","Test message body");
        //then
        verify(mailSender).send(message);
    }
}