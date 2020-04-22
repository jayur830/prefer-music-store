package com.prefer_music_store.app.repo;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface StoreDAO {
    String getStoreId(@Param("username") String username);
    List<Map<String, Object>> getStores();
}