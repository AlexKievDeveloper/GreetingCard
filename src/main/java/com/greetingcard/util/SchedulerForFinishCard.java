package com.greetingcard.util;

import com.greetingcard.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
class SchedulerForFinishCard {
    private final CardService cardService;

    @Async
    @Scheduled(cron = "0 0 0 * * *")
    public void reportCurrentData() {
        LocalDate localDate = LocalDate.now();
        cardService.finishCards(localDate);
        log.info("Finish cards " + localDate);
    }

}