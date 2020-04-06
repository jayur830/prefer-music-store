package com.prefer_music_store.app.repo;

import com.prefer_music_store.app.security.CustomUserDetails;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface UserAuthDAO {
    void signUp(CustomUserDetails user);
    CustomUserDetails getUserByUsername(@Param("username") String username);
    void updateLoginFailureCount(@Param("username") String username);
    int getLoginFailureCount(@Param("username") String username);
    void disableAccount(@Param("username") String username);
    void initLoginFailureCount(@Param("username") String username);
    void updatePassword(Map<String, Object> params);
}