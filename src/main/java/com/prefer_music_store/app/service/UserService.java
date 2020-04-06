package com.prefer_music_store.app.service;

import com.prefer_music_store.app.repo.UserVO;
import com.prefer_music_store.app.security.CustomUserDetails;

import java.util.Map;

public interface UserService {
    void signUp(UserVO userVO);
    String findUsername(Map<String, Object> params);
    boolean existUserByUsernameAndEmail(Map<String, Object> params);
    UserVO getUserInfo(String username);
    void editUserInfo(UserVO userVO);
    void updateRatingHistory(String userId, String ratingDatetime);
}