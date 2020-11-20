package com.greetingcard.service;

import com.greetingcard.dao.jdbc.FlywayConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitWebConfig(value = FlywayConfig.class)
class EmailServiceTest {
    @Autowired
    EmailService service;

    @Test
    void sendMail() {
        //service.sendMail("klisch.roma@gmail.com","Test Subject","https://www.youtube.com/?gl=UA&tab=r1");
        for (int i = 0; i < 5; i++) {
            service.sendMail("yshinkaren@gmail.com","Test Subject","https://www.youtube.com/?gl=UA&tab=r1");
            service.sendMail("glushkov18@gmail.com","Test Subject","https://www.youtube.com/?gl=UA&tab=r1");
        }

    }


}