package com.prefer_music_store.app.controller;

import com.prefer_music_store.app.service.PlaylistService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
public class PlaylistController {
    @Resource(name = "playlistService")
    private PlaylistService playlistService;

    @GetMapping("/admin_playlist_action")
    public List<Map<String, Object>> adminPlaylistAction(@RequestParam("username") String username) {
        return this.playlistService.getAdminStorePlaylist(username);
    }

    @GetMapping("/current_playlist_action")
    public List<Map<String, Object>> currentPlaylistAction(
            @RequestParam("username") String username,
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude) {
        System.out.println("username: " + (username.isEmpty() ? "empty" : username));
        return this.playlistService.getCurrentStorePlaylist(username.isEmpty() ? null : username, latitude, longitude);
    }

    @GetMapping("/user_playlist_action")
    public List<Map<String, Object>> userPlaylistAction(@RequestParam("username") String username) {
        return this.playlistService.getUserPlaylist(username);
    }

    @Transactional
    @GetMapping("/search_action")
    public List<Map<String, Object>> searchAction(
            @RequestParam("username") String username,
            @RequestParam("keyword") String keyword) {
        return this.playlistService.getPlaylistByKeyword(username, keyword);
    }
}
