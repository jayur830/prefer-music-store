package com.prefer_music_store.app.controller;

import com.prefer_music_store.app.engine.MainEngine;
import com.prefer_music_store.app.repo.UserVO;
import com.prefer_music_store.app.security.CustomUserDetails;
import com.prefer_music_store.app.security.CustomUserDetailsService;
import com.prefer_music_store.app.service.PlaylistService;
import com.prefer_music_store.app.service.UserRatingService;
import com.prefer_music_store.app.service.UserService;
import com.prefer_music_store.app.util.MapConverter;
import com.prefer_music_store.app.util.MessageUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DataController {
    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "userDetailsService")
    private CustomUserDetailsService userDetailsService;
    @Resource(name = "playlistService")
    private PlaylistService playlistService;
    @Resource(name = "ratingService")
    private UserRatingService ratingService;
    @Resource(name = "passwordEncoder")
    private PasswordEncoder passwordEncoder;

    @Resource(name = "mainEngine")
    private MainEngine mainEngine;

    @PostMapping("/is_auth")
    public Map<String, Object> isAuth() {
        Map<String, Object> json = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = !auth.getName().equals("anonymousUser");
        if (auth.getPrincipal() != null)
            json = MapConverter.convertToHashMap(
                    new String[] { "username", "auth" },
                    new Object[] { authenticated ? auth.getName() : null, authenticated });
        return json;
    }

    @PostMapping("/check_username")
    public String checkUsername(@RequestParam("username") String username) {
        return "{ \"valid\": " + !this.userDetailsService.exist(username) + " }";
    }

    @Transactional
    @PostMapping("/sign_up_action")
    public void signUpAction(UserVO userVO) {
        this.userService.signUp(userVO);
        this.userDetailsService.signUp(new CustomUserDetails(userVO.getUsername(), userVO.getPassword()));
    }

    @PostMapping("/find_username_action")
    public String findUsernameAction(
            @RequestParam("name") String name,
            @RequestParam("birth") String birth,
            @RequestParam("email") String email) {
        String username = this.userService.findUsername(
                MapConverter.convertToHashMap(
                        new String[] { "name", "birth", "email" },
                        new Object[] { name, birth, email }));
        return String.format("{ \"username\": %s }", username == null ? "null" :
                "\"" + username.substring(0, 3) + username.substring(3).replaceAll(".", "*") + "\"");
    }

    @Transactional
    @PostMapping("/find_password_action")
    public String findPasswordAction(
            @RequestParam("username") String username,
            @RequestParam("email") String email) {
        Map<String, Object> params = MapConverter.convertToHashMap(
                new String[] { "username", "email" },
                new Object[] { username, email });
        boolean valid = this.userService.existUserByUsernameAndEmail(params);
        if (valid) this.userDetailsService.resetPassword(params);
        return "{ \"valid\": " + valid + " }";
    }

    @Transactional
    @PostMapping("/get_user_info")
    public UserVO getUserInfo(@RequestParam("username") String username) {
        UserDetails userAuth = this.userDetailsService.loadUserByUsername(username);
        UserVO user = this.userService.getUserInfo(username);
        user.setPassword(userAuth.getPassword());
        return user;
    }

    @Transactional
    @PostMapping("/edit_user_info")
    public void editUserInfo(UserVO userVO) {
        this.userService.editUserInfo(userVO);
        if (userVO.getPassword() != null && !userVO.getPassword().isEmpty())
            this.userDetailsService.changePassword(
                    userVO.getUsername(), userVO.getPassword());
    }

    @Transactional
    @PostMapping(value = "/is_valid", produces = "application/text; charset=utf-8")
    public String isValid(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {
        UserDetails user = this.userDetailsService.loadUserByUsername(username);
        boolean valid = this.passwordEncoder.matches(password, user.getPassword());

        return String.format("{ \"valid\": %s, \"error\": \"%s\" }",
                valid, valid ? "" : MessageUtils.getMessage("error.BadCredentials"));
    }

    @Transactional
    @PostMapping("/delete_user_info")
    public void deleteUserInfo(@RequestParam("username") String username) {
        this.userService.deleteUserInfo(username);
    }

    @GetMapping("/playlist_action")
    public List<Map<String, Object>> playlistAction(@RequestParam("username") String username) {
        return this.playlistService.getPlaylist(username);
    }

    @Transactional
    @GetMapping("/search_action")
    public List<Map<String, Object>> searchAction(
            @RequestParam("username") String username,
            @RequestParam("keyword") String keyword) {
        return this.playlistService.getPlaylistByKeyword(username, keyword);
    }

    @Transactional
    @GetMapping("/rating_action")
    public void ratingAction(
            @RequestParam("user_id") String userId,
            @RequestParam("item_id") String itemId,
            @RequestParam("rating") double rating,
            @RequestParam("rating_datetime") String ratingDatetime) {
        this.ratingService.rating(userId, itemId, rating);
        this.userService.updateRatingHistory(userId, ratingDatetime);
    }

    @GetMapping("/exec")
    public Map<String, Object> executeServer(@RequestParam("username") String username) {
        Map<String, Object> json = new HashMap<>();
        json.put("playlist", null);
        json.put("serverStarted", false);

        String storeId = this.userService.getStoreId(username);

        if (!this.mainEngine.isStarted(storeId)) {
            this.mainEngine.init(storeId);
            while (!this.mainEngine.isUpdatedPlaylist(storeId));
            json.replace("playlist", this.playlistService.getPlaylist(null));
            json.replace("serverStarted", true);
        } else this.mainEngine.destroy(storeId);
        return json;
    }

    @GetMapping("/is_started")
    public boolean isServerStarted(@RequestParam("username") String username) {
        System.out.println(username);
        String storeId = this.userService.getStoreId(username);
        boolean started = this.mainEngine.isStarted(storeId);
        System.out.println(started);
        return started;
    }
}
