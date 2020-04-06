package com.prefer_music_store.app.model.recommendation.algo;

import com.prefer_music_store.app.repo.PlaylistDAO;
import com.prefer_music_store.app.repo.UserRatingDAO;
import com.prefer_music_store.app.repo.UserVO;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import javax.annotation.Resource;
import java.util.*;

public class AgeGenderPlaylistRecommendAlgorithm implements PlaylistRecommendAlgorithm {
    private final double MIN = 40, MAX = 60;

    private double ratioValue = 10;

    private INDArray ageGenderRatio = Nd4j.zeros(6, 2);

    @Resource(name = "playlistDAO")
    private PlaylistDAO playlistDAO;
    @Resource(name = "ratingDAO")
    private UserRatingDAO ratingDAO;
    @Resource(name = "userTable")
    private Map<String, UserVO> userTable;

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
         * 1. 현재 접속중인 세션별 유저들로부터 성별 및 연령대 정보를 가져온다.
         * 2. 성별 및 연령대의 비율을 알아내어 6 * 2 행렬로 초기화한다.
         * */
        this.ageGenderRatio = Nd4j.zeros(6, 2);
        for (Map.Entry<String, UserVO> iter : this.userTable.entrySet()) {
            UserVO user = iter.getValue();
            int age = user.getGender(), gender = user.getGender();
            this.ageGenderRatio.putScalar(new int[] { age, gender }, this.ageGenderRatio.getInt(age, gender) + 1);
        }

        System.out.println(this.ageGenderRatio);
    }

    @Override
    public List<Object> predict() {
        /**
         * initFeatures()를 통해 초기화 된 행렬의 각 인덱스(나이, 성별)별로 DB로부터 곡들을 가져온다.
         * */
        List<Object> playlist = new ArrayList<>();

        Map<Integer, int[]> indexMapper = new TreeMap<>();
        for (int age = 0; age < this.ageGenderRatio.rows(); ++age)
            for (int gender = 0; gender < this.ageGenderRatio.columns(); ++gender)
                indexMapper.put(this.ageGenderRatio.getInt(age, gender), new int[] { age, gender });

        for (Map.Entry<Integer, int[]> iter : indexMapper.entrySet()) {
            int[] indexes = iter.getValue();
            int count = iter.getKey();
            Map<String, Object> params = new HashMap<>();
            params.put("age", indexes[0]);
            params.put("gender", indexes[1]);
            params.put("count", count);
            playlist.addAll(this.playlistDAO.getSongsByAgeGender(params));
        }
        return playlist;
    }

    @Override
    public void update() {
        /**
         * 1. 연령대별 평균 평점 반영 이력을 가져온다.
         * 2. 남녀별 평균 평점 반영 이력을 가져온다.
         * 3. 각각에 대해 업데이트 한다.
         * 4. DB 내 분포도 값 중 일정 범위를 넘는지 체크한다. 일정 범위를 초과했을 경우 정규화한다.
         * 5. DB 내 성별 및 연령대 Flag 값을 갱신시킨다.
         * */
        Set<Integer> itemIdList = updatePreferenceDistribution();
        checkPreferenceDistribution();
        setFlagsPreferenceDistribution(itemIdList);
    }

    private Set<Integer> updatePreferenceDistribution() {
        List<Map<String, Object>> ratingsGroupByAge = this.ratingDAO.getAvgRatingsGroupByAge(),
                ratingsGroupByGender = this.ratingDAO.getAvgRatingsGroupByGender();
        Set<Integer> itemIdList = new HashSet<>();

        for (Map<String, Object> ratingInfo : ratingsGroupByAge) {
            itemIdList.add((Integer) ratingInfo.get("item_id"));
            this.playlistDAO.updateAgeParameters(ratingInfo);
        }
        for (Map<String, Object> ratingInfo : ratingsGroupByGender) {
            itemIdList.add((Integer) ratingInfo.get("item_id"));
            ratingInfo.replace("gender", ratingInfo.get("gender").equals(0) ? "female" : "male");
            this.playlistDAO.updateGenderParameters(ratingInfo);
        }
        return itemIdList;
    }

    private void checkPreferenceDistribution() {
        List<Map<String, Object>> itemsOverRange;

        itemsOverRange = this.playlistDAO.getSongsOverAgePreferenceRange();
        for (Map<String, Object> item : itemsOverRange) {
            int itemId = (int) item.get("item_id");
            item.remove("item_id");
            norm(item);
            item.put("item_id", itemId);
            this.playlistDAO.resetAgePreferenceDistribution(item);
        }

        itemsOverRange = this.playlistDAO.getSongsOverGenderPreferenceRange();
        for (Map<String, Object> item : itemsOverRange) {
            int itemId = (int) item.get("item_id");
            item.remove("item_id");
            norm(item);
            item.put("item_id", itemId);
            this.playlistDAO.resetGenderPreferenceDistribution(item);
        }
    }

    private void norm(Map<String, Object> item) {
        INDArray vector = Nd4j.create(1, item.size());
        String[] keyNames = new String[item.size()];
        int index = 0;
        for (Map.Entry<String, Object> iter : new TreeMap<>(item).entrySet()) {
            keyNames[index] = iter.getKey();
            vector.putScalar(new int[]{0, index++}, (double) iter.getValue());
        }

        double min = (double) vector.minNumber(), max = (double) vector.maxNumber();
        vector.sub(min);
        vector.mul((this.MAX - this.MIN) / (max - min));
        vector.add(this.MIN);

        for (int i = 0; i < keyNames.length; ++i)
            item.replace(keyNames[i], vector.getDouble(0, i));
    }

    private void setFlagsPreferenceDistribution(Set<Integer> itemIdList) {
        for (int itemId : itemIdList) {
            Map<String, Object> params = new HashMap<>();
            params.put("item_id", itemId);

            Map<String, Object> agePreferences = this.playlistDAO.getAgePreferenceDistribution(itemId);
            params.put("flag", argMax(agePreferences));
            this.playlistDAO.setAgeMaxPreferenceFlag(params);

            Map<String, Object> genderPreferences = this.playlistDAO.getGenderPreferenceDistribution(itemId);
            params.replace("flag", argMax(genderPreferences));
            this.playlistDAO.setGenderMaxPreferenceFlag(params);
        }
    }

    private int argMax(Map<String, Object> preferences) {
        double max = Double.NEGATIVE_INFINITY;
        int maxIndex = -1, currentIndex = -1;
        for (Map.Entry<String, Object> iter : new TreeMap<>(preferences).entrySet()) {
            ++currentIndex;
            if (max < (double) iter.getValue()) {
                max = (double) iter.getValue();
                maxIndex = currentIndex;
            }
        }
        return maxIndex;
    }
}
