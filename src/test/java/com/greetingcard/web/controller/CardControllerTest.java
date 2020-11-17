package com.greetingcard.web.controller;

import com.greetingcard.entity.Card;
import com.greetingcard.entity.Status;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {
    private MockMvc mockMvc;
    @InjectMocks
    private CardController controller;
    @Mock
    private CardService cardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).apply(sharedHttpSession()).build();
    }

    @Test
    void getCard() throws Exception {
        User user = User.builder().id(2).build();
        Card card1 = Card.builder().name("card1").user(user).build();
        when(cardService.getCardAndCongratulationByCardId(1, 2)).thenReturn(card1);
        mockMvc.perform(get("/api/v1/card")
                .param("id", "1")
                .sessionAttr("user", user)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.id").value("0"))
                .andExpect(jsonPath("$.name").value("card1"))
                .andExpect(status().isOk());
        verify(cardService).getCardAndCongratulationByCardId(1, 2);
    }

    @Test
    void getCardNoAccessOrCard() throws Exception {
        User user = User.builder().id(-1).build();
        when(cardService.getCardAndCongratulationByCardId(1, -1)).thenReturn(null);
        mockMvc.perform(get("/api/v1/card")
                .param("id", "1")
                .sessionAttr("user", user)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Sorry, you are not a member of this congratulation"))
                .andExpect(status().isForbidden());
        verify(cardService).getCardAndCongratulationByCardId(1, -1);
    }

    @Test
    void createCard() throws Exception {
        String json = "{\"name\":\"test\"}";
        User user = User.builder().id(2).build();
        Card card1 = Card.builder().name("test").user(user).build();
        when(cardService.createCard(card1)).thenReturn(100L);

        mockMvc.perform(post("/api/v1/card")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(jsonPath("$.id").value("100"))
                .andExpect(status().isCreated());
        verify(cardService).createCard(card1);
    }

    @Test
    void doPut() throws Exception {
        doNothing().when(cardService).changeCardStatus(Status.ISOVER, 1);
        mockMvc.perform(put("/api/v1/card/{id}/status", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(cardService).changeCardStatus(Status.ISOVER, 1);
    }

    @Test
    void doDelete() throws Exception {
        User user = User.builder().id(1).build();
        doNothing().when(cardService).deleteCardById(1, 1);
        mockMvc.perform(delete("/api/v1/card/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .sessionAttr("user", user)
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(cardService).deleteCardById(1, 1);
    }
}