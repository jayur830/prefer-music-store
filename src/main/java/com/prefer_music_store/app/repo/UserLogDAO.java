package com.prefer_music_store.app.repo;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserLogDAO {
    void setLoginDatetime(Map<String, Object> params);
    void setLogoutDatetime(Map<String, Object> params);
    List<String> getCurrentLoginUsers();
    double getAvgActiveTime(@Param("user_id") String userId);
    void deleteUserLog(@Param("user_id") String userId);
}