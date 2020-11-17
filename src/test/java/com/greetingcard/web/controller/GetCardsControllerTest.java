package com.greetingcard.web.controller;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@ExtendWith(MockitoExtension.class)
class GetCardsControllerTest {
    private MockMvc mockMvc;
    @InjectMocks
    private GetCardsController controller;
    @Mock
    private CardService cardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).apply(sharedHttpSession()).build();
    }

    @Test
    void getCardsAll() throws Exception {
        User user = User.builder().id(1).build();
        Card card1 = Card.builder().name("card1").user(user).build();
        Card card2 = Card.builder().name("card2").user(user).build();
        List<Card> list = new ArrayList<>();
        list.add(card1);
        list.add(card2);

        when(cardService.getCards(1, "all")).thenReturn(list);

        mockMvc.perform(get("/api/v1/cards?type=all")
                .sessionAttr("user", user)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$[0].id").value("0"))
                .andExpect(jsonPath("$[0].name").value("card1"))
                .andExpect(jsonPath("$[1].name").value("card2"))
                .andExpect(status().isOk());
        verify(cardService).getCards(1, "all");
    }

    @Test
    void getCardsAllNoAccessorCards() throws Exception {
        User user = User.builder().id(100).build();

        when(cardService.getCards(100, "all")).thenReturn(null);

        mockMvc.perform(get("/api/v1/cards?type=all")
                .sessionAttr("user", user)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Sorry, you do not have cards"))
                .andExpect(status().isOk());
        verify(cardService).getCards(100, "all");
    }
}