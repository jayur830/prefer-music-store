package com.prefer_music_store.app.model.recommendation.algo;

import com.prefer_music_store.app.model.mf.MatrixFactorization;
import com.prefer_music_store.app.model.mf.Nd4jMatrixFactorization;
import com.prefer_music_store.app.repo.PlaylistDAO;
import com.prefer_music_store.app.repo.UserDAO;
import com.prefer_music_store.app.repo.UserRatingDAO;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UserHistoryPlaylistRecommendAlgorithm implements PlaylistRecommendAlgorithm {
    @Resource(name = "userDAO")
    private UserDAO userDAO;
    @Resource(name = "playlistDAO")
    private PlaylistDAO playlistDAO;
    @Resource(name = "ratingDAO")
    private UserRatingDAO ratingDAO;

    @Resource(name = "userIndexToId")
    private List<String> userIndexToId;
    @Resource(name = "itemIndexToId")
    private List<Integer> itemIndexToId;
    @Resource(name = "userIdToIndex")
    private Map<String, Integer> userIdToIndex;
    @Resource(name = "itemIdToIndex")
    private Map<Integer, Integer> itemIdToIndex;

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
    public void initFeatures() {}

    @Override
    public void initFeatures(Object param) {}

    @Override
    public List<Object> predict() { return null; }

    public List<Integer> predict(String userId, int nList) {
        // 전체 플레이리스트를 담을 리스트를 생성한다.
        List<Integer> playlist = new ArrayList<>();

        double[][] predict = this.mf.predict(), matrix = this.mf.getR();

        // u: 유저의 인덱스, 행에 해당
        Integer u = this.userIdToIndex.get(userId);
        // u번째 유저가 존재하면 다음을 수행한다.
        if (u != null) {
            // 추천 결과를 보관하고 그것을 MF의 예측값 순으로 오름차순으로 정렬하는 TreeMap을 생성한다.
            Map<Double, Integer> recommend = new TreeMap<>();
            // i: 아이템의 인덱스, 열에 해당
            for (int i = 0; i < predict[u].length; ++i)
                // u번째 유저가 i번째 아이템에 실제로는 평점(matrix[u][i])을 반영하지 않았다면(matrix[u][i] == 0)
                // recommend에 u번째 유저의 i번째 아이템에 대한 MF의 예측 평점 값을 Key로, 아이템의 인덱스 i를 Value로 추가한다.
                if (matrix[u][i] == 0) recommend.put(predict[u][i], this.itemIndexToId.get(i));

            for (Map.Entry<Double, Integer> iter : recommend.entrySet()) {
                playlist.add(iter.getValue());
                if (playlist.size() == nList) break;
            }
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

        // DB로부터 모든 유저들의 평점 반영 이력들을 모조리 가져온다.
        List<Map<String, Object>> ratingHistory = this.ratingDAO.getRatings();
        // 이력 데이터가 1개 이상 존재한다면
        if (!ratingHistory.isEmpty()) {
            // 모든 이력 데이터를 탐색한다.
            for (Map<String, Object> ratingInfo : ratingHistory) {
                // 유저의 ID -> MF 상에서의 유저의 인덱스
                int user = this.userIdToIndex.get(ratingInfo.get("user_id"));
                // 아이템의 ID -> MF 상에서의 아이템의 인덱스
                int item = this.itemIdToIndex.get(ratingInfo.get("item_id"));
                // 평점
                double rating = (double) ratingInfo.get("rating");

                // MF에 유저, 아이템, 평점 데이터를 할당한다.
                this.mf.put(user, item, rating);
            }
            // 백그라운드에서 MF를 20회 학습시킨다.
            Thread thread = new Thread(() -> mf.fit(20));
            thread.setDaemon(true);
            thread.start();
        }
    }
}
