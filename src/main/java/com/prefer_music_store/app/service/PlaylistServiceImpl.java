package com.prefer_music_store.app.service;

import com.prefer_music_store.app.model.recommendation.algo.UserHistoryPlaylistRecommendAlgorithm;
import com.prefer_music_store.app.repo.PlaylistDAO;
import com.prefer_music_store.app.repo.StoreDAO;
import com.prefer_music_store.app.util.MapConverter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service("playlistService")
public class PlaylistServiceImpl implements PlaylistService {
    @Resource(name = "playlistDAO")
    private PlaylistDAO playlistDAO;
    @Resource(name = "storeDAO")
    private StoreDAO storeDAO;

    @Resource(name = "userHistoryPlaylistRecommendAlgorithm")
    private UserHistoryPlaylistRecommendAlgorithm historyAlgorithm;

    @Override
    public List<Map<String, Object>> getAdminStorePlaylist(String username) {
        return this.playlistDAO.getCurrentStorePlaylist(this.storeDAO.getStoreId(username));
    }

    @Override
    public List<Map<String, Object>> getCurrentStorePlaylist(String username, double latitude, double longitude) {
        List<Map<String, Object>> stores = this.storeDAO.getStores();
        if (stores == null || stores.isEmpty()) return null;
        Map<Double, String> distances = new TreeMap<>();
        for (Map<String, Object> store : stores) {
            double _latitude = (double) store.get("latitude"),
                    _longitude = (double) store.get("longitude");
            distances.put(Math.sqrt(
                    (latitude - _latitude) * (latitude - _latitude) +
                            (longitude - _longitude) * (longitude - _longitude)), (String) store.get("store_id"));
        }
        Map.Entry<Double, String> store = distances.entrySet().iterator().next();
        double distance = store.getKey();
        String storeId = store.getValue();
        if (username == null)
            return distance <= 60.0 ? this.playlistDAO.getCurrentStorePlaylist(storeId) : null;
        else return distance <= 60.0 ? this.playlistDAO.getCurrentStorePlaylistAndRatingsOfUser(
                MapConverter.convertToHashMap(
                        new String[] { "store_id", "user_id" },
                        new Object[] { storeId, username })) : null;
    }

    @Override
    public List<Map<String, Object>> getUserPlaylist(String username) {
        List<Integer> list = historyAlgorithm.predict(username, 20);
        List<Map<String, Object>> playlist = new ArrayList<>();
        Map<String, Object> params = MapConverter.convertToHashMap(
                new String[] { "user_id", "item_id" },
                new Object[] { username, null });
        for (int songId : list) {
            params.replace("item_id", songId);
            playlist.add(this.playlistDAO.getUserPlaylistAndRatingsOfUser(params));
        }
        return playlist;
    }

    @Override
    public List<Map<String, Object>> getPlaylistByKeyword(String username, String keyword) {
        // username == null 은 비회원을 의미한다.
        // 비회원일 경우 검색 키워드에 대한 플레이리스트만 가져온다.
        if (username == null) return this.playlistDAO.getPlaylistByKeyword(keyword);
        // 회원일 경우 검색 키워드에 대한 플레이리스트와
        // 그 곡들에 대한 해당 유저의 평점 반영 정보까지 같이 가져온다.
        else return this.playlistDAO.getPlaylistAndRatingsByKeywordOfUser(
                MapConverter.convertToHashMap(
                        new String[] { "username", "keyword" },
                        new Object[] { username, keyword }));
    }
}