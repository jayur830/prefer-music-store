package com.prefer_music_store.app.engine;

import com.prefer_music_store.app.repo.PlaylistDAO;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

public class ServerDestroyer {
    @Resource(name = "playlistDAO")
    private PlaylistDAO playlistDAO;

    @Transactional
    public void destroy() {
        this.playlistDAO.deleteCurrentPlaylist();
    }
}
