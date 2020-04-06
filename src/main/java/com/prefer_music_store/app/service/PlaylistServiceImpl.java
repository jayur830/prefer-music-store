package com.prefer_music_store.app.service;

import com.prefer_music_store.app.repo.PlaylistDAO;
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
        return username == null ?
                this.playlistDAO.getCurrentPlaylist() :
                this.playlistDAO.getCurrentPlaylistAndRatings(username);
    }

    @Override
    public List<Map<String, Object>> getPlaylistByKeyword(String username, String keyword) {
        if (username == null) return this.playlistDAO.getCurrentPlaylistByKeyword(keyword);
        else {
            Map<String, Object> params = new HashMap<>();
            params.put("username", username);
            params.put("keyword", keyword);
            return this.playlistDAO.getCurrentPlaylistAndRatingsByKeyword(params);
        }
    }
}