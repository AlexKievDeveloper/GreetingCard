package com.greetingcard.web.controller;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.RootApplicationContext;
import com.greetingcard.dao.jdbc.TestConfiguration;
import com.greetingcard.entity.UserInfo;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@SpringJUnitWebConfig(value = {TestConfiguration.class, RootApplicationContext.class})
@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@DataSet(value = {"languages.xml", "types.xml", "roles.xml", "statuses.xml", "users.xml", "cards.xml", "cardUser/cardUsers.xml",
        "congratulations.xml", "links.xml"},
        executeStatementsBefore = "SELECT setval('users_user_id_seq', 10);",
        cleanAfter = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CardUserControllerSystemTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Flyway flyway;

    private final String URL_ADD_MEMBER = "/api/v1/card/{id}/user";
    private final String URL_GET_MEMBERS = "/api/v1/card/{id}/users";
    private final String URL_DELETE_MEMBERS = "/api/v1/card/{id}/users";

    @BeforeAll
    void dbSetUp() {
        flyway.migrate();
    }

    @BeforeEach
    void setMockMvc() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(sharedHttpSession())
                .build();
    }

    @Test
    @DisplayName("Add member to card - login is empty")
    void addUserMemberIfEmptyLogin() throws Exception {
        TestWebUtils.loginAsUserId(2);
        String json = "{\"login\":\"\"}";
        mockMvc.perform(post(URL_ADD_MEMBER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Login of user is empty"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Add member to card - login does not exist")
    void addUserMemberIfNotExistingLogin() throws Exception {
        TestWebUtils.loginAsUserId(2);
        String json = "{\"login\":\"u1111\"}";
        mockMvc.perform(post(URL_ADD_MEMBER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Login does not exist"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Add member to card - user is already added")
    void addUserMemberIfAlreadyMember() throws Exception {
        TestWebUtils.loginAsUserId(1);
        String json = "{\"login\":\"admin\"}";
        mockMvc.perform(post(URL_ADD_MEMBER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("User is already member"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Add member to card - user is not admin for card")
    void addUserMemberIfNotAdminAddsMember() throws Exception {
        TestWebUtils.loginAsUserId(2);
        String json = "{\"login\":\"new\"}";
        mockMvc.perform(post(URL_ADD_MEMBER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Only card owner can add users"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Add member to card - card is over")
    void addUserMemberCardIsOver() throws Exception {
        TestWebUtils.loginAsUserId(2);
        String json = "{\"login\":\"new\"}";
        mockMvc.perform(post(URL_ADD_MEMBER, 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Card is already finished"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Add member to card - success")
    @ExpectedDataSet("cardUser/cardUsersAdded.xml")
    void addUserMember() throws Exception {
        TestWebUtils.loginAsUserId(1);
        String json = "{\"login\":\"new\"}";
        mockMvc.perform(post(URL_ADD_MEMBER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get users by card - not admin")
    void getUsersByCardIdNotAdmin() throws Exception {
        TestWebUtils.loginAsUserId(1);
        mockMvc.perform(get(URL_GET_MEMBERS, 2))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Only card owner can get users"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get users by card (members only) - no members")
    void getUsersByCardIdForCardWithoutMembers() throws Exception {
        TestWebUtils.loginAsUserId(2);
        mockMvc.perform(get(URL_GET_MEMBERS, 3))
                .andDo(print())
                .andExpect(content().string("[]"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get users by card (members only) - some members")
    void getUsersByCardIdForCardWithSomeMembers() throws Exception {
        UserInfo expectedUser1 = UserInfo.builder().id(1)
                .firstName("admin")
                .lastName("admin")
                .login("admin")
                .email("@admin")
                .build();

        UserInfo expectedUser4 = UserInfo.builder().id(4)
                .firstName("testName")
                .lastName("testLastName")
                .login("testLogin")
                .email("testEmail")
                .pathToPhoto("testPathToPhoto")
                .build();

        TestWebUtils.loginAsUserId(2);
        mockMvc.perform(get(URL_GET_MEMBERS, 2))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(expectedUser1.getId()))
                .andExpect(jsonPath("$[0].firstName").value(expectedUser1.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(expectedUser1.getLastName()))
                .andExpect(jsonPath("$[0].login").value(expectedUser1.getLogin()))
                .andExpect(jsonPath("$[1].id").value(expectedUser4.getId()))
                .andExpect(jsonPath("$[1].firstName").value(expectedUser4.getFirstName()))
                .andExpect(jsonPath("$[1].lastName").value(expectedUser4.getLastName()))
                .andExpect(jsonPath("$[1].login").value(expectedUser4.getLogin()))
                .andExpect(jsonPath("$[1].pathToPhoto").value(expectedUser4.getPathToPhoto()));
    }

    @Test
    @DisplayName("Delete some users - success")
    @ExpectedDataSet(value = {"cardUser/cardUsersListDeleted.xml", "cardUser/congratulationsDeleted.xml"})
    void deleteUsers() throws Exception {
        TestWebUtils.loginAsUserId(2);
        String json = "[{\"id\":\"1\"}, {\"id\":\"4\"}]";
        mockMvc.perform(delete(URL_DELETE_MEMBERS, 2)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete some users - users already deleted")
    @DataSet({"languages.xml", "types.xml", "roles.xml", "statuses.xml", "users.xml", "cards.xml", "cardUser/cardUsersListDeleted.xml", "cardUser/congratulationsDeleted.xml"})
    @ExpectedDataSet({"cardUser/cardUsersListDeleted.xml", "cardUser/congratulationsDeleted.xml"})
    void deleteUsersNothingToDelete() throws Exception {
        TestWebUtils.loginAsUserId(2);
        String json = "[{\"id\":\"1\"}, {\"id\":\"4\"}]";
        mockMvc.perform(delete(URL_DELETE_MEMBERS, 2)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete users - empty list of users")
    void deleteUsersEmptyList() throws Exception {
        TestWebUtils.loginAsUserId(2);
        String json = "[]";
        mockMvc.perform(delete(URL_DELETE_MEMBERS, 2)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete users - not admin deletes")
    void deleteUsersEmptyListNotAdmin() throws Exception {
        TestWebUtils.loginAsUserId(1);
        String json = "[]";
        mockMvc.perform(delete(URL_DELETE_MEMBERS, 2)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("Only card owner can delete users"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Leave card")
    void leaveCard() throws Exception {
        TestWebUtils.loginAsUserId(1);
        mockMvc.perform(delete("/api/v1/card/{id}/user", 2L)
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk());
    }

}