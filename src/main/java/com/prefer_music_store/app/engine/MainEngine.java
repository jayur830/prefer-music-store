package com.prefer_music_store.app.engine;

import com.prefer_music_store.app.model.recommendation.algo.PlaylistRecommendAlgorithm;
import com.prefer_music_store.app.model.recommendation.api.Recommender;
import com.prefer_music_store.app.repo.PlaylistDAO;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

public class MainEngine {
    @Resource(name = "playlistDAO")
    private PlaylistDAO playlistDAO;

    @Resource(name = "recommender")
    private Recommender<PlaylistRecommendAlgorithm> recommender;

    @Transactional
    public void init() {
        Thread thread = new Thread(() -> {
            while (true) {
                /**
                 * 현재 유저들의 상태 정보를 파악하여 이를 모델에 초기화한다.
                 *
                 * */
                this.recommender.initFeatures();

                /**
                 * 초기화된 데이터에 따라 플레이리스트를 예측한다.
                 * */
                List<Object> playlist = this.recommender.recommend(10);

                /**
                 * current_playlist 테이블 내 모든 튜플들을 지우고 새로운 플레이리스트를 반영한다.
                 * */
                this.playlistDAO.deleteCurrentPlaylist();
                for (Object songId : playlist) this.playlistDAO.insertCurrentSong((int) songId);

                /**
                 * 플레이리스트 내 모든 곡들을 순서대로 재생한다.
                 * */
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                /**
                 * 플레이리스트 내 모든 곡들의 재생이 끝나면 모델을 학습한다.
                 * - 성별, 연령대 선호도 분포 갱신
                 * - MF 파라미터 갱신
                 * */
                this.recommender.update();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Transactional
    public void destroy() {
        System.out.println("destroy");
        this.playlistDAO.deleteCurrentPlaylist();
    }
}
