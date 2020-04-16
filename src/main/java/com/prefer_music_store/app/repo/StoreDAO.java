package com.prefer_music_store.app.repo;

import org.apache.ibatis.annotations.Param;

public interface StoreDAO {
    String getStoreId(@Param("username") String username);
}
