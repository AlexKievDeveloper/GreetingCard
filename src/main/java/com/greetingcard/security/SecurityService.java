package com.greetingcard.security;

import com.greetingcard.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface SecurityService {

    User login(String login, String password);

    void save(User user);

    void update(User user, MultipartFile file);

    User findById(long id);

    User findByLogin(String login);
}
