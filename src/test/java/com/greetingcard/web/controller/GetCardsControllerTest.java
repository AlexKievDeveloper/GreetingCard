package com.greetingcard.web.controller;

import com.alibaba.fastjson.JSON;
import com.greetingcard.entity.Card;
import com.greetingcard.entity.User;
import com.greetingcard.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@ExtendWith(MockitoExtension.class)
class GetCardsControllerTest {
    private MockMvc mockMvc;
    @InjectMocks
    GetCardsController controller;
    @Mock
    CardService cardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).apply(sharedHttpSession()).build();
    }

    @Test
    void getCards() throws Exception {
        User user = User.builder().id(1).build();
        Card card1 = Card.builder().name("card1").user(user).build();
        Card card2 = Card.builder().name("card2").user(user).build();
        List<Card> list = new ArrayList<>();
        list.add(card1);
        list.add(card2);
        String ddd = JSON.toJSONString(list);

        when(cardService.getCards(1, "all")).thenReturn(list);

        mockMvc.perform(get("/api/v1/cards?type=all")
                .sessionAttr("user", user)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        verify(cardService).getCards(1, "all");
    }
}