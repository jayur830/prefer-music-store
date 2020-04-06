package com.prefer_music_store.app.model.recommendation.api;

import java.util.*;

public class Recommender<T extends RecommendAlgorithm> extends HashMap<String, T> {
    private RandomRecommendAlgorithm randomRecommendAlgorithm;

    public void setRecommendAlgorithms(Map<String, T> algorithms) {
        putAll(algorithms);
    }

    public void setRandomRecommendAlgorithm(RandomRecommendAlgorithm randomRecommendAlgorithm) {
        this.randomRecommendAlgorithm = randomRecommendAlgorithm;
    }

    public void initFeatures() {
        for (Entry<String, T> algorithm : entrySet())
            algorithm.getValue().initFeatures();
    }

    public Recommender<T> initFeatures(String algorithmName) {
        get(algorithmName).initFeatures();
        return this;
    }

    public List<Object> recommend(int nList) {
        List<Object> items = new ArrayList<>();
        Map<String, List<Object>> predictedItemMapper = new HashMap<>();
        Set<Object> overlapRemover = new HashSet<>();
        int size = 0;

        for (Entry<String, T> algorithm : entrySet()) {
            List<Object> predictedList = algorithm.getValue().predict();
            //overlapRemover.addAll(predictedList);
            for (int i = 0; i < predictedList.size(); ++i) {
                if (!overlapRemover.contains(predictedList.get(i)))
                    overlapRemover.add(predictedList.get(i));
                else predictedList.remove(i--);
            }
            predictedItemMapper.put(algorithm.getKey(), predictedList);
            size += predictedList.size();
        }

        System.out.println();
        for (Entry<String, List<Object>> iter : predictedItemMapper.entrySet())
            System.out.println(iter.getKey() + ": " + iter.getValue());

        overlapRemover.clear();

        if (size > nList) {
            // 아이템 수를 간추린다.
            double total = 0;
            Map<String, Integer> ratio = new HashMap<>();
            for (Entry<String, T> iter : entrySet())
                total = iter.getValue().getRatio();
            for (Entry<String, T> iter : entrySet())
                ratio.put(iter.getKey(), (int) (iter.getValue().getRatio() * (double) nList / total));

            for (Entry<String, List<Object>> iter : predictedItemMapper.entrySet())
                items.addAll(iter.getValue().subList(0,
                        ratio.get(iter.getKey()) > iter.getValue().size() ?
                                iter.getValue().size() : ratio.get(iter.getKey())));
        } else if (size < nList) {
            for (Entry<String, List<Object>> iter :
                    predictedItemMapper.entrySet())
                overlapRemover.addAll(iter.getValue());
            // 랜덤으로 아이템을 채운다.
            while (overlapRemover.size() < nList)
                overlapRemover.addAll(this.randomRecommendAlgorithm.predict());
            items.addAll(Arrays.asList(overlapRemover.toArray()).subList(0, nList));
        } else
            for (Entry<String, List<Object>> iter :
                    predictedItemMapper.entrySet())
                items.addAll(iter.getValue());

        return items;
    }

    public List<Object> recommend() {
        List<Object> items = new ArrayList<>();
        for (Entry<String, T> algorithm : entrySet()) {
            List<Object> predictedList = algorithm.getValue().predict();
            for (Object element : items) predictedList.remove(element);
            items.addAll(predictedList);
        }
        return items;
    }

    public void update() {
        for (Entry<String, T> algorithm : entrySet())
            algorithm.getValue().update();
    }
}