package com.prefer_music_store.app.service;

import com.prefer_music_store.app.repo.UserRatingDAO;
import com.prefer_music_store.app.util.MapConverter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service("ratingService")
public class UserRatingServiceImpl implements UserRatingService {
    @Resource(name = "ratingDAO")
    private UserRatingDAO ratingDAO;

    @Override
    public void rating(String userId, String itemId, double rating) {
        // 평점 반영 이력 데이터
        Map<String, Object> params = MapConverter.convertToHashMap(
                new String[] { "user_id", "item_id", "rating" },
                new Object[] { userId, Integer.parseInt(itemId), rating });

        // 해당 유저가 해당 곡에 대해 이전에 평점을 반영한 이력이 존재한다면
        if (this.ratingDAO.isRated(params))
            // 기존의 이력을 업데이트하고
            this.ratingDAO.updateRating(params);
        // 그렇지 않으면 평점 반영 이력 데이터를 새로 추가한다.
        else this.ratingDAO.insertRating(params);
    }
}
