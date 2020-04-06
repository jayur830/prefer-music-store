package com.prefer_music_store.app.controller;

import com.prefer_music_store.app.repo.UserVO;
import com.prefer_music_store.app.security.CustomUserDetails;
import com.prefer_music_store.app.security.CustomUserDetailsService;
import com.prefer_music_store.app.service.PlaylistService;
import com.prefer_music_store.app.service.UserRatingService;
import com.prefer_music_store.app.service.UserService;
import com.prefer_music_store.app.util.MapConverter;
import org.springframework.security.core.userdetails.UserDetails;
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
        if (userVO.getPassword() != null)
            this.userDetailsService.changePassword(
                    userVO.getUsername(), userVO.getPassword());
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
}
