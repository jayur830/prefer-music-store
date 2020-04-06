package com.prefer_music_store.app.service;

import com.prefer_music_store.app.repo.UserRatingDAO;
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
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("item_id", Integer.parseInt(itemId));
        params.put("rating", rating);
        if (this.ratingDAO.isRated(params))
            this.ratingDAO.updateRating(params);
        else this.ratingDAO.insertRating(params);
    }
}
