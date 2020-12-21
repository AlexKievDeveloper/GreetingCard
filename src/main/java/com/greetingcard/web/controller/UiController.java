package com.greetingcard.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiController {
    @GetMapping(value = {"/", "/login", "/profile", "/home", "/forgot_password", "/recover_password/{hash}", "/login", "/signup",
            "/card_users/{id}", "/change_password", "/cards/{type}", "/create_card/{id}", "/edit_card/{id}", "/add_block/{idCard}",
            "/edit_block/{idBlock}", "/card/{idCard}/card_link/{hash}", "/preview"})
    public String forward404() {
        return "forward:/index.html";
    }
}