package com.prefer_music_store.app.controller;

import com.prefer_music_store.app.repo.UserVO;
import com.prefer_music_store.app.security.CustomUserDetails;
import com.prefer_music_store.app.security.CustomUserDetailsService;
import com.prefer_music_store.app.util.MapConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
public class UserAuthController {
    @Resource(name = "userDetailsService")
    private CustomUserDetailsService userDetailsService;

    @PostMapping("/is_auth")
    public Map<String, Object> isAuth() {
        Map<String, Object> json = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = !auth.getName().equals("anonymousUser");
        String authority = auth.getAuthorities().toArray()[0].toString();
        if (auth.getPrincipal() != null)
            json = MapConverter.convertToHashMap(
                    new String[] { "username", "auth", "authority" },
                    new Object[] { authenticated ? auth.getName() : null, authenticated, authority });
        return json;
    }

    @PostMapping("/check_username")
    public String checkUsername(@RequestParam("username") String username) {
        return "{ \"valid\": " + !this.userDetailsService.exist(username) + " }";
    }

    @Transactional
    @PostMapping("/sign_up_auth_action")
    public void signUpAction(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {
        this.userDetailsService.signUp(new CustomUserDetails(username, password));
    }

    //@Transactional
    @PostMapping("/find_password_action")
    public void findPasswordAction(
            @RequestParam("username") String username,
            @RequestParam("email") String email) {
        this.userDetailsService.resetPassword(
                MapConverter.convertToHashMap(
                        new String[] { "username", "email" },
                        new Object[] { username, email }));
    }

    @Transactional
    @PostMapping("/get_password")
    public String getPassword(@RequestParam("username") String username) {
        return this.userDetailsService.loadUserByUsername(username).getPassword();
    }
}
