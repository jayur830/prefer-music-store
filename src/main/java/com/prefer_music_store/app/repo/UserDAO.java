package com.prefer_music_store.app.repo;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserDAO {
    void signUp(UserDTO userDTO);
    String findUsername(Map<String, Object> params);
    boolean existUserByUsernameAndEmail(Map<String, Object> params);
    UserDTO getUserInfo(@Param("username") String username);
    void editUserInfo(UserDTO userDTO);
    void deleteUserInfo(@Param("username") String username);
    List<String> getUsers();
}
