package com.prefer_music_store.app.repo;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserRatingDAO {
    boolean isRated(Map<String, Object> params);
    void insertRating(Map<String, Object> params);
    void updateRating(Map<String, Object> params);
    void deleteUserRating(@Param("user_id") String userId);
    List<Map<String, Object>> getRatings();
    List<Map<String, Object>> getAvgRatingsGroupByAge();
    List<Map<String, Object>> getAvgRatingsGroupByGender();
}
