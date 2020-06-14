package com.prefer_music_store.app.service;

import com.prefer_music_store.app.repo.UserDTO;

import java.util.Map;

public interface UserService {
    void signUp(UserDTO userDTO);
    String findUsername(Map<String, Object> params);
    boolean existUserByUsernameAndEmail(Map<String, Object> params);
    UserDTO getUserInfo(String username);
    void editUserInfo(UserDTO userDTO);
    void deleteUserInfo(String username);
    String getStoreId(String username);
    void rating(String userId, String itemId, double rating);
}