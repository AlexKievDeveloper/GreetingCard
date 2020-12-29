package com.greetingcard.web.controller;

import com.greetingcard.entity.User;
import com.greetingcard.web.security.user.ApplicationUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class WebUtils {
    public static Long getCurrentUserId() {
        User user = getCurrentUser();
        return user.getId();
    }

    public static User getCurrentUser() {
        ApplicationUserDetails userDetails = (ApplicationUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }
}
