package com.greetingcard.util;

import com.greetingcard.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
class SchedulerForFinishCard {
    private final CardService cardService;
    @Async
    //@Scheduled(cron = "*/3 * * * * *")
    //@Scheduled(cron = "*/5 * * * * ?")
    @Scheduled(fixedRate = 1000)
    public void reportCurrentData() {
        System.out.println("Scheduler working: " + new Date());
    }

}