package com.prefer_music_store.app.engine;

import com.prefer_music_store.app.model.recommendation.algo.PlaylistRecommendAlgorithm;
import com.prefer_music_store.app.model.recommendation.api.Recommender;
import com.prefer_music_store.app.repo.PlaylistDAO;
import com.prefer_music_store.app.util.MapConverter;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainEngine {
    @Resource(name = "playlistDAO")
    private PlaylistDAO playlistDAO;

    @Resource(name = "recommender")
    private Recommender<PlaylistRecommendAlgorithm> recommender;

    private Map<String, Thread> threadPool = new HashMap<>();
    private Map<String, Boolean> updatedPlaylist = new HashMap<>();

    public boolean isStarted(String storeId) {
        return this.threadPool.containsKey(storeId);
    }

    @Transactional
    public void init(String storeId) {
        Thread thread = new Thread(() -> {
            while (isStarted(storeId)) {
                this.updatedPlaylist.replace(storeId, false);
                /**
                 * 현재 유저들의 상태 정보를 파악하여 이를 모델에 초기화한다.
                 * */
                this.recommender.initFeatures();

                if (!isStarted(storeId)) throw new RuntimeException("Thread interrupted");
                /**
                 * 초기화된 데이터에 따라 플레이리스트를 예측한다.
                 * */
                List<Object> playlist = this.recommender.recommend(10);

                if (!isStarted(storeId)) throw new RuntimeException("Thread interrupted");
                /**
                 * current_playlist 테이블 내 모든 튜플들을 지우고 새로운 플레이리스트를 반영한다.
                 * */
                this.playlistDAO.deleteCurrentPlaylist(storeId);

                if (!isStarted(storeId)) throw new RuntimeException("Thread interrupted");

                for (Object songId : playlist) this.playlistDAO.insertCurrentSong(
                        MapConverter.convertToHashMap(
                                new String[] { "song_id", "store_id" },
                                new Object[] { songId, storeId }));

                this.updatedPlaylist.replace(storeId, true);

                if (!isStarted(storeId)) throw new RuntimeException("Thread interrupted");
                /**
                 * 플레이리스트 내 모든 곡들을 순서대로 재생한다.
                 * */
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!isStarted(storeId)) throw new RuntimeException("Thread interrupted");

                /**
                 * 플레이리스트 내 모든 곡들의 재생이 끝나면 모델을 학습한다.
                 * - 성별, 연령대 선호도 분포 갱신
                 * - MF 파라미터 갱신
                 * */
                this.recommender.update();

                if (!isStarted(storeId)) throw new RuntimeException("Thread interrupted");
            }
        });
        thread.setDaemon(true);
        thread.start();

        this.threadPool.put(storeId, thread);
        this.updatedPlaylist.put(storeId, false);
    }

    @Transactional
    public void destroy(String storeId) {
        System.out.println("destroy");
        this.playlistDAO.deleteCurrentPlaylist(storeId);
        this.threadPool.get(storeId).interrupt();
        this.threadPool.remove(storeId);
        this.updatedPlaylist.remove(storeId);
    }

    public boolean isUpdatedPlaylist(String storeId) {
        return this.updatedPlaylist.get(storeId);
    }
}
