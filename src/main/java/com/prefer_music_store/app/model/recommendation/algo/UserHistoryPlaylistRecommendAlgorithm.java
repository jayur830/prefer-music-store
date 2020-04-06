package com.prefer_music_store.app.model.recommendation.algo;

import com.prefer_music_store.app.model.mf.MatrixFactorization;
import com.prefer_music_store.app.model.mf.Nd4jMatrixFactorization;
import com.prefer_music_store.app.repo.PlaylistDAO;
import com.prefer_music_store.app.repo.UserDAO;
import com.prefer_music_store.app.repo.UserLogDAO;
import com.prefer_music_store.app.repo.UserRatingDAO;
import com.prefer_music_store.app.util.UserRFMCalculator;

import javax.annotation.Resource;
import java.util.*;

public class UserHistoryPlaylistRecommendAlgorithm implements PlaylistRecommendAlgorithm {
    @Resource(name = "userDAO")
    private UserDAO userDAO;
    @Resource(name = "playlistDAO")
    private PlaylistDAO playlistDAO;
    @Resource(name = "ratingDAO")
    private UserRatingDAO ratingDAO;
    @Resource(name = "userLogDAO")
    private UserLogDAO userLogDAO;

    @Resource(name = "rfmCalculator")
    private UserRFMCalculator rfmCalculator;

    @Resource(name = "userIndexToId")
    private List<String> userIndexToId;
    @Resource(name = "itemIndexToId")
    private List<Integer> itemIndexToId;
    @Resource(name = "userIdToIndex")
    private Map<String, Integer> userIdToIndex;
    @Resource(name = "itemIdToIndex")
    private Map<Integer, Integer> itemIdToIndex;

    // Key: Index of user, Value: RFM score of user
    @Resource(name = "rfmScores")
    private Map<Integer, Integer> rfmScores;

    private double ratioValue = 10;

    private MatrixFactorization mf;

    public void init() {
        this.userIndexToId.addAll(this.userDAO.getUsers());
        this.itemIndexToId.addAll(this.playlistDAO.getSongs());
        for (int i = 0; i < userIndexToId.size(); ++i)
            this.userIdToIndex.put(this.userIndexToId.get(i), i);
        for (int i = 0; i < itemIndexToId.size(); ++i)
            this.itemIdToIndex.put(this.itemIndexToId.get(i), i);

        this.mf = new Nd4jMatrixFactorization(
                userIndexToId.size(), itemIndexToId.size(),
                100, 40, 40);
    }

    @Override
    public void setRatio(double ratioValue) {
        this.ratioValue = ratioValue;
    }

    @Override
    public double getRatio() {
        return this.ratioValue;
    }

    @Override
    public void initFeatures() {
        /**
         * 1. 현재 로그인한 유저(회원, 비회원)들을 모두 알아낸다.
         * 2. DB로부터 유저의 활동 이력들을 모조리 가져온다.
         * 3. RFM 점수를 계산하여 유저별로 scores에 할당한다.
         * */
        this.rfmScores.clear();
        List<String> users = this.userLogDAO.getCurrentLoginUsers();
        for (String userId : users) {
            Map<String, Object> ratingHistory = this.userDAO.getRatingHistory(userId);
            String recentRatingDatetime = (String) ratingHistory.get("recent_rating_datetime");
            long ratingCount = (long) ratingHistory.get("rating_count");
            double avgActiveTime = this.userLogDAO.getAvgActiveTime(userId);
            int[] rfm = this.rfmCalculator.getRFM(recentRatingDatetime, ratingCount, avgActiveTime);
            this.rfmScores.put(this.userIdToIndex.get(userId), rfm[0] + rfm[1] + rfm[2]);
        }
    }

    @Override
    public List<Object> predict() {
        List<Object> playlist = new ArrayList<>();
        Map<Integer, List<Object>> scoreOrderMapper = new TreeMap<>();
        if (!this.rfmScores.isEmpty()) {
            double[][] predict = this.mf.predict(), matrix = this.mf.getR();
            for (int u = 0; u < predict.length; ++u) {
                if (this.rfmScores.get(u) == null) continue;

                Map<Double, Integer> recommend = new TreeMap<>();
                for (int i = 0; i < predict[u].length; ++i)
                    if (matrix[u][i] == 0) recommend.put(predict[u][i], i);

                int n = this.rfmScores.get(u);
                scoreOrderMapper.put(n, new ArrayList<>());
                Iterator<Map.Entry<Double, Integer>> iterator = recommend.entrySet().iterator();
                for (int count = 0; count < n && iterator.hasNext(); ++count)
                    scoreOrderMapper.get(n).add(this.itemIndexToId.get(iterator.next().getValue()));
            }
            for (Map.Entry<Integer, List<Object>> iter : scoreOrderMapper.entrySet())
                playlist.addAll(iter.getValue());
        }
        return playlist;
    }

    @Override
    public void update() {
        /**
         * 1. DB로부터 평점 반영 이력들을 모조리 가져온다.
         * 2. mf.put() 메소드로 새로운 원본 행렬로 초기화한다.
         * 3. mf.fit() 메소드로 적절한 횟수만큼 백그라운드에서 학습한다.
         * */
        List<Map<String, Object>> ratingHistory = this.ratingDAO.getRatings();
        if (!ratingHistory.isEmpty()) {
            for (Map<String, Object> ratingInfo : ratingHistory) {
                int user = this.userIdToIndex.get(ratingInfo.get("user_id"));
                int item = this.itemIdToIndex.get(ratingInfo.get("item_id"));
                double rating = (double) ratingInfo.get("rating");
                this.mf.put(user, item, rating);
            }
            Thread thread = new Thread(() -> mf.fit(20));
            thread.setDaemon(true);
            thread.start();
        }
    }
}
