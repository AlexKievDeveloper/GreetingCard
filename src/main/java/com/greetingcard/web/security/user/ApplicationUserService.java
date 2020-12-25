package com.greetingcard.web.security.user;

import com.greetingcard.entity.User;
import com.greetingcard.security.SecurityService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component("applicationUserService")
@AllArgsConstructor
public class ApplicationUserService implements UserDetailsService {
    private SecurityService securityService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = securityService.findByLogin(username);
        return ApplicationUserDetails.fromUserToAppUserDetails(user);
    }
}
