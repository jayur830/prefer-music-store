package com.prefer_music_store.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/sign_up")
    public String signUp() {
        return "sign_up";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/main")
    public String main() {
        return "main";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/find_username")
    public String findUsername() {
        return "find_username";
    }

    @GetMapping("/find_password")
    public String findPassword() {
        return "find_password";
    }

    @GetMapping("/delete_user_auth")
    public String deleteUserAuth() {
        return "delete_user_auth";
    }

    @GetMapping("/user_info")
    public String userInfo() {
        return "user_info";
    }

    @GetMapping("/access_denied")
    public String accessDenied() {
        return "error";
    }
}
