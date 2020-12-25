package com.greetingcard.web.controller;

import com.greetingcard.entity.User;
import com.greetingcard.web.security.user.ApplicationUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class TestWebUtils {
    public static void loginAsUserId(long id) {
        User user = User.builder().id(id).build();
        ApplicationUserDetails userDetails = ApplicationUserDetails.fromUserToAppUserDetails(user);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext()
                .setAuthentication(auth);
    }

    public static void loginAsUser(User user) {
        ApplicationUserDetails userDetails = ApplicationUserDetails.fromUserToAppUserDetails(user);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext()
                .setAuthentication(auth);
    }
}
