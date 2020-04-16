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
         * 3. RFM 점수를 계산하여 유저별로 rfmScores에 할당한다.
         * */

        // 현재 로그인한 유저들의 RFM 점수를 초기화한다.
        this.rfmScores.clear();
        // 현재 로그인한 유저들의 아이디들을 DB로부터 모조리 가져온다.
        List<String> users = this.userLogDAO.getCurrentLoginUsers();

        // 현재 로그인한 유저들을 모두 탐색한다.
        for (String userId : users) {
            // 특정 유저에 대한 이력 데이터를 가져온다.
            Map<String, Object> ratingHistory = this.userDAO.getRatingHistory(userId);
            // 최근 평점 반영 시간
            String recentRatingDatetime = (String) ratingHistory.get("recent_rating_datetime");
            // 총 평점 반영 횟수
            long ratingCount = (long) ratingHistory.get("rating_count");
            // 평균 활동 시간
            double avgActiveTime = this.userLogDAO.getAvgActiveTime(userId);
            // 위에서 구한 세 가지 데이터를 인수값으로 하여 RFM 점수를 구한다.
            int[] rfm = this.rfmCalculator.getRFM(recentRatingDatetime, ratingCount, avgActiveTime);
            // rfmScores에 Key를 유저의 아이디로, Value를 RFM의 합산 점수로 넣는다.
            this.rfmScores.put(this.userIdToIndex.get(userId), rfm[0] + rfm[1] + rfm[2]);
        }
    }

    @Override
    public List<Object> predict() {
        // 전체 플레이리스트를 담을 리스트를 생성한다.
        List<Object> playlist = new ArrayList<>();
        // RFM 점수(Key)를 기준으로 오름차순으로 정렬되는 TreeMap을 생성한다.
        Map<Integer, List<Object>> scoreOrderMapper = new TreeMap<>();

        // initFeatures에서 구한 RFM 계산 결과가 1개 이상 존재할 경우
        if (!this.rfmScores.isEmpty()) {
            // predict: MF의 아웃풋, matrix: 실제로 유저들이 특정 아이템들에 대해 평점을 매긴 원본 행렬 R
            double[][] predict = this.mf.predict(), matrix = this.mf.getR();

            // 모든 유저들을 순차적으로 탐색
            // u: 유저의 인덱스, 행에 해당
            for (int u = 0; u < predict.length; ++u) {
                // u번째 유저의 RFM 점수가 없다면, 즉 u번째 유저가 아직 아무런 활동을 하지 않았다면 해당 루프는 건너뛴다.
                if (this.rfmScores.get(u) == null) continue;

                // 추천 결과를 보관하고 그것을 MF의 예측값 순으로 오름차순으로 정렬하는 TreeMap을 생성한다.
                Map<Double, Integer> recommend = new TreeMap<>();
                // i: 아이템의 인덱스, 열에 해당
                for (int i = 0; i < predict[u].length; ++i)
                    // u번째 유저가 i번째 아이템에 실제로는 평점(matrix[u][i])을 반영하지 않았다면(matrix[u][i] == 0)
                    // recommend에 u번째 유저의 i번째 아이템에 대한 MF의 예측 평점 값을 Key로, 아이템의 인덱스 i를 Value로 추가한다.
                    if (matrix[u][i] == 0) recommend.put(predict[u][i], i);

                // u번째 유저의 RFM 점수를 가져온다.
                int n = this.rfmScores.get(u);
                // u번째 유저의 RFM 점수를 Key로, 빈 리스트를 Value로 삽입하고,
                scoreOrderMapper.put(n, new ArrayList<>());
                // 위에서 구한 recommend를 탐색하기 위한 이터레이터를 통해
                Iterator<Map.Entry<Double, Integer>> iterator = recommend.entrySet().iterator();
                // 아이템을 n개 추가한다. scoreOrderMapper가 TreeMap이기 때문에 Key 값에 해당하는 RFM 점수를 기준으로 데이터 삽입 시마다 오름차순으로 정렬된다.
                for (int count = 0; count < n && iterator.hasNext(); ++count)
                    scoreOrderMapper.get(n).add(this.itemIndexToId.get(iterator.next().getValue()));
            }

            // RFM 점수순으로 추가했던 모든 아이템들을 전체 플레이리스트로 추가한다.
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
