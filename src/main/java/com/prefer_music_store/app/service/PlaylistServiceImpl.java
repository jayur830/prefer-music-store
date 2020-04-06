package com.prefer_music_store.app.service;

import com.prefer_music_store.app.repo.PlaylistDAO;
import com.prefer_music_store.app.util.MapConverter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("playlistService")
public class PlaylistServiceImpl implements PlaylistService {
    @Resource(name = "playlistDAO")
    private PlaylistDAO playlistDAO;

    @Override
    public List<Map<String, Object>> getPlaylist(String username) {
        // username == null 은 비회원을 의미한다.
        return username == null ?
                // 비회원일 경우 그냥 플레이리스트만,
                this.playlistDAO.getCurrentPlaylist() :
                // 회원일 경우 해당 플레이리스트에 대한 해당 유저의 평점 반영 정보까지 같이 가져온다.
                this.playlistDAO.getCurrentPlaylistAndRatings(username);
    }

    @Override
    public List<Map<String, Object>> getPlaylistByKeyword(String username, String keyword) {
        // username == null 은 비회원을 의미한다.
        // 비회원일 경우 검색 키워드에 대한 플레이리스트만 가져온다.
        if (username == null) return this.playlistDAO.getCurrentPlaylistByKeyword(keyword);
        // 회원일 경우 검색 키워드에 대한 플레이리스트와
        // 그 곡들에 대한 해당 유저의 평점 반영 정보까지 같이 가져온다.
        else return this.playlistDAO.getCurrentPlaylistAndRatingsByKeyword(
                MapConverter.convertToHashMap(
                        new String[] { "username", "keyword" },
                        new Object[] { username, keyword }));
    }
}