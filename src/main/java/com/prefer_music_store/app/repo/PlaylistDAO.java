package com.prefer_music_store.app.repo;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PlaylistDAO {
    List<Map<String, Object>> getCurrentPlaylistAndRatings(@Param("username") String username);
    List<Map<String, Object>> getCurrentPlaylistAndRatingsByKeyword(Map<String, Object> params);
    List<Map<String, Object>> getCurrentPlaylist();
    List<Map<String, Object>> getCurrentPlaylistByKeyword(@Param("keyword") String keyword);

    List<Integer> getSongsByAgeGender(Map<String, Object> params);

    List<Integer> getSongs();
    Map<String, Object> getArtistAndSongName(@Param("item_id") int itemId);

    void updateAgeParameters(Map<String, Object> params);
    void updateGenderParameters(Map<String, Object> params);
    List<Map<String, Object>> getSongsOverAgePreferenceRange();
    List<Map<String, Object>> getSongsOverGenderPreferenceRange();
    void resetAgePreferenceDistribution(Map<String, Object> params);
    void resetGenderPreferenceDistribution(Map<String, Object> params);

    Map<String, Object> getAgePreferenceDistribution(@Param("item_id") int itemId);
    Map<String, Object> getGenderPreferenceDistribution(@Param("item_id") int itemId);
    void setAgeMaxPreferenceFlag(Map<String, Object> params);
    void setGenderMaxPreferenceFlag(Map<String, Object> params);

    void deleteCurrentPlaylist(@Param("store_id") String storeId);
    void insertCurrentSong(Map<String, Object> params);
}
