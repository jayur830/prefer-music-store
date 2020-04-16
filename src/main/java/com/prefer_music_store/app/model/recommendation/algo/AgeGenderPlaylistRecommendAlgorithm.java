package com.prefer_music_store.app.model.recommendation.algo;

import com.prefer_music_store.app.model.imgproc.AgeGenderEstimation;
import com.prefer_music_store.app.model.imgproc.FaceDetection;
import com.prefer_music_store.app.repo.PlaylistDAO;
import com.prefer_music_store.app.repo.UserRatingDAO;
import com.prefer_music_store.app.repo.UserVO;
import com.prefer_music_store.app.util.MapConverter;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

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

    @Resource(name = "ageGenderEstimation")
    private AgeGenderEstimation ageGenderEstimation;
    @Resource(name = "faceDetection")
    private FaceDetection faceDetection;

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
        // 6 * 2의 0으로 채워진 행렬을 먼저 생성한다.
        // ageGenderRatio: 6행 2열의 행렬로써 6개의 행은 연령대를, 2개의 열은 성별을, 그리고 각각의 위치의 값들은 해당 나이와 성별에 해당하는 인원 수를 의미한다.
        this.ageGenderRatio = Nd4j.zeros(6, 2);

//        // userTable: 현재 로그인한 유저들의 상태 정보를 모아놓은 테이블
//        // 테이블 내 모든 유저들의 정보로부터 나이와 성별 정보 추출
//        for (Map.Entry<String, UserVO> iter : this.userTable.entrySet()) {
//            // 특정 유저 정보를 얻어온다.
//            UserVO user = iter.getValue();
//            // 특정 유저 정보에서 나이와 성별만을 얻어온다.
//            int age = user.getGender(), gender = user.getGender();
//            // ageGenderRatio 행렬에서 기존의 age 행, gender 열 위치에 있던 값을 1 증가시킨다.
//            this.ageGenderRatio.putScalar(new int[] { age, gender }, this.ageGenderRatio.getInt(age, gender) + 1);
//        }

        Mat image = getImage();
        List<Rect> detectedObjects = this.faceDetection.predict(image);
        for (Rect rect : detectedObjects) {
            Mat face = new Mat(rect.height, rect.width, CvType.CV_8UC3);
            for (int i = 0; i < face.rows(); ++i)
                for (int j = 0; j < face.cols(); ++j)
                    face.put(i, j, image.get(i + rect.y, j + rect.x));
            int[] ageGender = this.ageGenderEstimation.predict(face);
            int age = ageGender[1], gender = ageGender[0];

            System.out.printf("age: %d, gender: %smale\n", age, gender == 0 ? "fe" : "");

            // ageGenderRatio 행렬에서 기존의 age 행, gender 열 위치에 있던 값을 1 증가시킨다.
            this.ageGenderRatio.putScalar(new int[] { age / 10 - 1, gender }, this.ageGenderRatio.getInt(age / 10 - 1, gender) + 1);
        }

        System.out.println(this.ageGenderRatio);
    }

    @Override
    public List<Object> predict() {
        /**
         * initFeatures()를 통해 초기화 된 행렬의 각 인덱스(나이, 성별)별로 DB로부터 곡들을 가져온다.
         * */
        // 전체 플레이리스트를 담을 리스트 생성
        List<Object> playlist = new ArrayList<>();

        // TreeMap: 트리 형태의 맵 객체로써, 삽입되는 즉시 키의 순서대로 정렬된다.
        // Key: ageGenderRatio 행렬의 age 행, gender 열의 값
        // Value: age 행, gender 열 위치
        // indexMapper: 나이, 성별 비율의 인원 수에 해당되는 나이와 성별을 매핑시키기 위한 객체
        Map<Integer, int[]> indexMapper = new TreeMap<>();

        // ageGenderRatio 로부터 값 세팅
        for (int age = 0; age < this.ageGenderRatio.rows(); ++age)
            for (int gender = 0; gender < this.ageGenderRatio.columns(); ++gender)
                indexMapper.put(this.ageGenderRatio.getInt(age, gender), new int[] { age, gender });

        // indexMapper의 처음부터 끝까지 탐색
        for (Map.Entry<Integer, int[]> iter : indexMapper.entrySet()) {
            // 2개의 원소를 갖는 1차원 배열, 0번째는 나이, 1번째는 성별에 해당
            int[] indexes = iter.getValue();

            // 해당 나이와 성별에 대한 인원 수
            int count = iter.getKey();

            // age, gender, count를 DB에 질의하여 나온 플레이리스트 결과를 전체 플레이리스트에 추가한다.
            playlist.addAll(this.playlistDAO.getSongsByAgeGender(
                    MapConverter.convertToHashMap(
                            new String[] { "age", "gender", "count" },
                            new Object[] { indexes[0], indexes[1], count })));
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

        // 성별, 연령대 선호도 분포의 수치를 업데이트하고 그 결과(아이템들의 ID)를 얻어온다.
        Set<Integer> itemIdList = updatePreferenceDistribution();
        // 방금 업데이트한 선호도 분포의 값 중에 0~100 사이 범위 미만이거나 초과하는 값이 있는지 검증한다.
        checkPreferenceDistribution();
        // 선호도 내에서 가장 높은 비율의 성별, 나이로 플래그 값을 갱신한다.
        setFlagsPreferenceDistribution(itemIdList);
    }

    private Mat getImage() {
        return Imgcodecs.imread(getClass().getResource("/model/sample.jpg").getFile().substring(1));
    }

    private Set<Integer> updatePreferenceDistribution() {
        // 연령대별, 남녀별 평균평점을 얻어온다.
        List<Map<String, Object>>
                ratingsGroupByAge = this.ratingDAO.getAvgRatingsGroupByAge(),
                ratingsGroupByGender = this.ratingDAO.getAvgRatingsGroupByGender();
        // 아이템들의 ID를 보관할 리스트(Set 타입)를 생성한다.
        Set<Integer> itemIdList = new HashSet<>();

        for (Map<String, Object> ratingInfo : ratingsGroupByAge) {
            // 연령대별 평균평점 정보로부터 특정 아이템을 아이템 리스트에 추가한다.
            itemIdList.add((Integer) ratingInfo.get("item_id"));
            // 해당 평점 반영 정보를 DB에 질의하여 연령대 선호도 분포를 업데이트 한다.
            this.playlistDAO.updateAgeParameters(ratingInfo);
        }

        for (Map<String, Object> ratingInfo : ratingsGroupByGender) {
            // 남녀별 평균평점 정보로부터 특정 아이템을 아이템 리스트에 추가한다.
            itemIdList.add((Integer) ratingInfo.get("item_id"));
            // DB에 질의하기 위해 정수 값으로 표현된 성별 데이터를 문자열로 치환한다.
            ratingInfo.replace("gender", ratingInfo.get("gender").equals(0) ? "female" : "male");
            // 해당 평점 반영 정보를 DB에 질의하여 성별 선호도 분포를 업데이트 한다.
            this.playlistDAO.updateGenderParameters(ratingInfo);
        }

        return itemIdList;
    }

    private void checkPreferenceDistribution() {
        // 0~100 범위를 벗어나는 값을 갖는 곡들을 담기 위한 객체를 생성한다.
        List<Map<String, Object>> itemsOverRange;

        // 연령대 선호도 분포에서 0~100 범위 미만 또는 초과하는 값을 포함하는 곡들을 가져온다.
        itemsOverRange = this.playlistDAO.getSongsOverAgePreferenceRange();
        for (Map<String, Object> item : itemsOverRange) {
            // 아이템의 ID 값을 가져온 뒤
            int itemId = (int) item.get("item_id");
            // item_id 키에 해당하는 키-값 데이터를 제거한다.
            item.remove("item_id");
            // 해당 아이템에 대하여 범위를 재조정한다.
            norm(item);
            // 다시 item_id 를 키로 하는 데이터를 추가한다.
            item.put("item_id", itemId);
            // 해당 아이템에 대해 새롭게 초기화된 연령대 선호도 분포를 DB에 반영한다.
            this.playlistDAO.resetAgePreferenceDistribution(item);
        }

        // 성별 선호도 분포에서 0~100 범위 미만 또는 초과하는 값을 포함하는 곡들을 가져온다.
        itemsOverRange = this.playlistDAO.getSongsOverGenderPreferenceRange();
        for (Map<String, Object> item : itemsOverRange) {
            // 아이템의 ID 값을 가져온 뒤
            int itemId = (int) item.get("item_id");
            // item_id 키에 해당하는 키-값 데이터를 제거한다.
            item.remove("item_id");
            // 해당 아이템에 대하여 범위를 재조정한다.
            norm(item);
            // 다시 item_id 를 키로 하는 데이터를 추가한다.
            item.put("item_id", itemId);
            // 해당 아이템에 대해 새롭게 초기화된 성별 선호도 분포를 DB에 반영한다.
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

        // 벡터의 최솟값과 최댓값을 구한다.
        double min = (double) vector.minNumber(), max = (double) vector.maxNumber();
        // 벡터의 모든 원소에서 벡터의 최솟값을 뺀다.
        vector = vector.sub(min);
        // 벡터의 모든 원소에 ((재조정하려는 범위의 최댓값 - 재조정하려는 범위의 최솟값) / (벡터의 최댓값 - 벡터의 최솟값)) 을 곱한다.
        vector = vector.mul((this.MAX - this.MIN) / (max - min));
        // 벡터의 모든 원소에 재조정하려는 범위의 최솟값을 더한다.
        vector = vector.add(this.MIN);

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
