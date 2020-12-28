package com.greetingcard.web.security.jwt;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.greetingcard.RootApplicationContext;
import com.greetingcard.dao.jdbc.TestConfiguration;
import com.greetingcard.entity.Language;
import com.greetingcard.entity.User;
import com.greetingcard.web.security.user.ApplicationUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@DataSet(value = {"languages.xml", "types.xml", "roles.xml", "statuses.xml", "users.xml", "cards.xml", "cardsUsers.xml",
        "congratulations.xml", "links.xml", "forgot_password_hashes.xml", "verify_email_hashes.xml"},
        executeStatementsBefore = "SELECT setval('users_user_id_seq', 10);",
        cleanAfter = true)
@SpringJUnitWebConfig(value = {TestConfiguration.class, RootApplicationContext.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JwtTokenVerifierFilterTest {

    @Autowired
    private JwtTokenVerifierFilter jwtTokenVerifierFilter;

    @Autowired
    JwtProvider jwtProvider;

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private HttpServletResponse servletResponse;

    @Mock
    private FilterChain filterChain;


    @Test
    void doFilterInternal() throws ServletException, IOException {

        String token = jwtProvider.generateToken("user");

        when(servletRequest.getHeader("Authorization")).thenReturn("Bearer " + token);

        jwtTokenVerifierFilter.doFilterInternal(servletRequest, servletResponse, filterChain);

        verify(filterChain).doFilter(servletRequest, servletResponse);
        ApplicationUserDetails applicationUserDetails = (ApplicationUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        User user = applicationUserDetails.getUser();

        assertEquals(2, user.getId());
        assertEquals("user", user.getFirstName());
        assertEquals("user", user.getLastName());
        assertEquals("user", user.getLogin());
        assertEquals("@user", user.getEmail());
        assertEquals("testPathToPhoto2", user.getPathToPhoto());
        assertEquals("gDE3fEwV4WEZhgiURMj/WMlTWP/cldaSptEMe2M+md8=", user.getPassword());
        assertEquals("salt", user.getSalt());
        assertEquals(Language.UKRAINIAN, user.getLanguage());
    }

    @Test
    void doFilterInternalWithoutHeader() throws ServletException, IOException {
        when(servletRequest.getHeader("Authorization")).thenReturn(null);
        //when
        jwtTokenVerifierFilter.doFilterInternal(servletRequest, servletResponse, filterChain);
        //then
        verify(filterChain).doFilter(servletRequest, servletResponse);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternalWithoutHeaderWithBearer() throws ServletException, IOException {
        when(servletRequest.getHeader("Authorization")).thenReturn("Basic ");
        //when
        jwtTokenVerifierFilter.doFilterInternal(servletRequest, servletResponse, filterChain);
        //then
        verify(filterChain).doFilter(servletRequest, servletResponse);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternalWithWrongToken() throws ServletException, IOException {
        when(servletRequest.getHeader("Authorization")).thenReturn("Bearer  1");
        //when
        jwtTokenVerifierFilter.doFilterInternal(servletRequest, servletResponse, filterChain);
        //then
        verify(filterChain).doFilter(servletRequest, servletResponse);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}