package com.prefer_music_store.app.service;

import java.util.List;
import java.util.Map;

public interface PlaylistService {
    List<Map<String, Object>> getPlaylist(String username);
    List<Map<String, Object>> getPlaylistByKeyword(String username, String keyword);
}