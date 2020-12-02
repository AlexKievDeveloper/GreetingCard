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

    /**
     * This method sends a real letter to the mailbox, so please be careful while running it :)
     * You can change the email to your own address (but in that case you also need to
     * change it in userWithRealEmail.xml dataset. Peace!
     *
     * To use your own mailbox as sender, you'll need to update email address credentials of
     * "mailSender" bean in applicationContext.xml. Also, you need to allow unsafe applications
     * access in your google account settings;
     */
    @Test
    void sendMail() {
        service.sendMail("nomarchia2@gmail.com","Test Subject","Test message body");
    }
}