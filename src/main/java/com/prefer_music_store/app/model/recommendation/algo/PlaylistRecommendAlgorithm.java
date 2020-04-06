package com.prefer_music_store.app.model.recommendation.algo;

import com.prefer_music_store.app.model.recommendation.api.RecommendAlgorithm;

import java.util.List;

public interface PlaylistRecommendAlgorithm extends RecommendAlgorithm {
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