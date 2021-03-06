package com.prefer_music_store.app.model.recommendation.api;

import java.util.List;

public interface RecommendAlgorithm {
    void setRatio(double ratioValue);
    double getRatio();
    void initFeatures();
    void initFeatures(Object param);
    List<Object> predict();
    void update();
}