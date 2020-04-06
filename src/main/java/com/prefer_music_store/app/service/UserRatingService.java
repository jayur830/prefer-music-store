package com.prefer_music_store.app.service;

public interface UserRatingService {
    void rating(String userId, String itemId, double rating);
}