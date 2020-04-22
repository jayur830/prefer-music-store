package com.prefer_music_store.app.model.recommendation.algo;

import com.prefer_music_store.app.model.recommendation.api.RandomRecommendAlgorithm;
import com.prefer_music_store.app.repo.PlaylistDAO;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlaylistRandomRecommendAlgorithm implements RandomRecommendAlgorithm {
    private Random random = new Random();

    @Resource(name = "itemIndexToId")
    private List<Integer> itemIndexToId;

    @Override
    public void setRatio(double ratioValue) {}

    @Override
    public double getRatio() {
        return 0;
    }

    @Override
    public void initFeatures() {}

    @Override
    public void initFeatures(Object param) {}

    @Override
    public List<Object> predict() {
        List<Object> list = new ArrayList<>();
        int n = this.random.nextInt(100);
        while (list.size() != n) list.add(this.itemIndexToId.get(Math.abs(this.random.nextInt(this.itemIndexToId.size()))));
        return list;
    }

    @Override
    public void update() {}
}
