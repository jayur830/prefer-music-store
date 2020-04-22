package com.prefer_music_store.app.service;

import java.util.List;
import java.util.Map;

public interface PlaylistService {
    List<Map<String, Object>> getAdminStorePlaylist(String username);
    List<Map<String, Object>> getCurrentStorePlaylist(String username, double latitude, double longitude);
    List<Map<String, Object>> getUserPlaylist(String username);
    List<Map<String, Object>> getPlaylistByKeyword(String username, String keyword);
}