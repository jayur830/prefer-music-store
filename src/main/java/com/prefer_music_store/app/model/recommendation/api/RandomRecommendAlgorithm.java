package com.prefer_music_store.app.model.recommendation.api;

import java.util.List;

public interface RandomRecommendAlgorithm extends RecommendAlgorithm {
    @Override
    void setRatio(double ratioValue);

    @Override
    double getRatio();

    @Override
    void initFeatures();

    @Override
    List<Object> predict();

    @Override
    void update();
}