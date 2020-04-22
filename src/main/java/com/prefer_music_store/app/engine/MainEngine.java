package com.prefer_music_store.app.engine;

import com.prefer_music_store.app.model.recommendation.algo.PlaylistRecommendAlgorithm;
import com.prefer_music_store.app.model.recommendation.algo.UserHistoryPlaylistRecommendAlgorithm;
import com.prefer_music_store.app.model.recommendation.api.Recommender;
import com.prefer_music_store.app.repo.PlaylistDAO;
import com.prefer_music_store.app.util.MapConverter;
import com.prefer_music_store.app.util.data_transfer.DataTransfer;
import com.prefer_music_store.app.util.data_transfer.DataTransferClient;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainEngine {
    @Resource(name = "playlistDAO")
    private PlaylistDAO playlistDAO;

    @Resource(name = "recommender")
    private Recommender<PlaylistRecommendAlgorithm> recommender;
    @Resource(name = "userHistoryPlaylistRecommendAlgorithm")
    private UserHistoryPlaylistRecommendAlgorithm historyAlgorithm;

    private Map<String, Thread> threadPool = new HashMap<>();
    private Map<String, Boolean> updatedPlaylist = new HashMap<>();
//    private Map<String, DataTransfer> sockets = new HashMap<>();

    public boolean isStarted(String storeId) {
        return this.threadPool.containsKey(storeId);
    }

    @Transactional
    public void init(String storeId) {
//        DataTransfer dataTransfer = new DataTransferClient("localhost", 41257);
        Thread thread = new Thread(() -> {
            while (isStarted(storeId)) {
                updatedPlaylist.replace(storeId, false);
                /**
                 * 현재 유저들의 상태 정보를 파악하여 이를 모델에 초기화한다.
                 * */
                String path = getClass().getResource("/model/input.jpg").getPath().substring(1);

//                if (!dataTransfer.receiveFile(path)) {
//                    dataTransfer.send("-1");
//                    continue;
//                } else dataTransfer.send("0");

                Mat img = Imgcodecs.imread(path);

                recommender
                        .initFeatures("ageGenderPlaylistRecommendAlgorithm", img);

                if (!isStarted(storeId)) throw new RuntimeException("Thread interrupted");
                /**
                 * 초기화된 데이터에 따라 플레이리스트를 예측한다.
                 * */
                List<Object> playlist = recommender.recommend(10);

                if (!isStarted(storeId)) throw new RuntimeException("Thread interrupted");
                /**
                 * current_playlist 테이블 내 모든 튜플들을 지우고 새로운 플레이리스트를 반영한다.
                 * */
                playlistDAO.deleteCurrentPlaylist(storeId);

                if (!isStarted(storeId)) throw new RuntimeException("Thread interrupted");

                for (Object songId : playlist) playlistDAO.insertCurrentSong(
                        MapConverter.convertToHashMap(
                                new String[] { "song_id", "store_id" },
                                new Object[] { songId, storeId }));

                updatedPlaylist.replace(storeId, true);

                if (!isStarted(storeId)) throw new RuntimeException("Thread interrupted");
                /**
                 * 플레이리스트 내 모든 곡들을 순서대로 재생한다.
                 * */

                int playlistSize = playlist.size();

//                dataTransfer.send(Integer.toString(playlistSize));

//                for (int i = 0; i < playlistSize; ++i) {
//                    Object songId = playlist.get(i);
//                    System.out.println("song_id: " + songId);
//                    try {
//                        URL resource = Class.forName("com.prefer_music_store.app.engine.MainEngine")
//                                .getResource("/audio/" + songId + ".mp3");
//                        if (resource == null) continue;
//                        do {
//                            dataTransfer.sendFile(new File(resource.getFile()));
//                            System.out.println("success to send the audio file");
//                        } while (dataTransfer.receive().equals("-1"));
//                    } catch (ClassNotFoundException e) {
//                        e.printStackTrace();
//                    }
//
//                    if (!isStarted(storeId)) throw new RuntimeException("Thread interrupted");
//
//                    while (true) {
//                        if (!isStarted(storeId)) throw new RuntimeException("Thread interrupted");
//
//                        System.out.println("wait for receiving signal");
//                        int signal = Integer.parseInt(dataTransfer.receive());
//                        System.out.println("received signal: " + signal);
//
//                        if (signal == 0) break;
//                        else if (signal == 1) {
//                            /**
//                             * 플레이리스트 내 모든 곡들의 재생이 끝나면 모델을 학습한다.
//                             * - 성별, 연령대 선호도 분포 갱신
//                             * - MF 파라미터 갱신
//                             * */
//                            recommender.update();
//                            historyAlgorithm.update();
//                        }
//
//                        if (!isStarted(storeId)) throw new RuntimeException("Thread interrupted");
//                    }
//                }
                try {
                    Thread.sleep(120000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                recommender.update();
                historyAlgorithm.update();
            }
        });
        thread.setDaemon(true);
        thread.start();

        this.threadPool.put(storeId, thread);
        this.updatedPlaylist.put(storeId, false);
//        this.sockets.put(storeId, dataTransfer);
    }

    @Transactional
    public void destroy(String storeId) {
        System.out.println("destroy");
        this.playlistDAO.deleteCurrentPlaylist(storeId);
        this.threadPool.get(storeId).interrupt();
        this.threadPool.remove(storeId);
        this.updatedPlaylist.remove(storeId);
//        this.sockets.get(storeId).close();
//        this.sockets.remove(storeId);
    }

    public boolean isUpdatedPlaylist(String storeId) {
        return this.updatedPlaylist.get(storeId);
    }
}
