package com.prefer_music_store.app.repo;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserDAO {
    void signUp(UserVO userVO);
    String findUsername(Map<String, Object> params);
    boolean existUserByUsernameAndEmail(Map<String, Object> params);
    UserVO getUserInfo(@Param("username") String username);
    void editUserInfo(UserVO userVO);
    void deleteUserInfo(@Param("username") String username);
    List<String> getUsers();
    Map<String, Object> getRatingHistory(@Param("username") String username);
    void updateRatingHistory(Map<String, Object> params);
}
