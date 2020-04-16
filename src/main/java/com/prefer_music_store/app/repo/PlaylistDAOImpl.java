package com.prefer_music_store.app.repo;

import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository("playlistDAO")
public class PlaylistDAOImpl implements PlaylistDAO {
    @Resource(name = "sqlSession")
    private SqlSessionTemplate sqlSession;

    private static String namespace = "mappers.PlaylistMapper";

    @Override
    public List<Map<String, Object>> getCurrentPlaylistAndRatings(@Param("username") String username) {
        return this.sqlSession.selectList(namespace + ".getCurrentPlaylistAndRatings", username);
    }

    @Override
    public List<Map<String, Object>> getCurrentPlaylistAndRatingsByKeyword(Map<String, Object> params) {
        return this.sqlSession.selectList(namespace + ".getCurrentPlaylistAndRatingsByKeyword", params);
    }

    @Override
    public List<Map<String, Object>> getCurrentPlaylist() {
        return this.sqlSession.selectList(namespace + ".getCurrentPlaylist");
    }

    @Override
    public List<Map<String, Object>> getCurrentPlaylistByKeyword(@Param("keyword") String keyword) {
        return this.sqlSession.selectList(namespace + ".getCurrentPlaylistByKeyword", keyword);
    }

    @Override
    public List<Integer> getSongsByAgeGender(Map<String, Object> params) {
        return this.sqlSession.selectList(namespace + ".getSongsByAgeGender", params);
    }

    @Override
    public List<Integer> getSongs() {
        return this.sqlSession.selectList(namespace + ".getSongs");
    }

    @Override
    public Map<String, Object> getArtistAndSongName(int itemId) {
        return this.sqlSession.selectOne(namespace + ".getArtistAndSongName", itemId);
    }

    @Override
    public void updateAgeParameters(Map<String, Object> params) {
        this.sqlSession.update(namespace + ".updateAgeParameters", params);
    }

    @Override
    public void updateGenderParameters(Map<String, Object> params) {
        this.sqlSession.update(namespace + ".updateGenderParameters", params);
    }

    @Override
    public List<Map<String, Object>> getSongsOverAgePreferenceRange() {
        return this.sqlSession.selectList(namespace + ".getSongsOverAgePreferenceRange");
    }

    @Override
    public List<Map<String, Object>> getSongsOverGenderPreferenceRange() {
        return this.sqlSession.selectList(namespace + ".getSongsOverGenderPreferenceRange");
    }

    @Override
    public void resetAgePreferenceDistribution(Map<String, Object> params) {
        this.sqlSession.update(namespace + ".resetAgePreferenceDistribution", params);
    }

    @Override
    public void resetGenderPreferenceDistribution(Map<String, Object> params) {
        this.sqlSession.update(namespace + ".resetGenderPreferenceDistribution", params);
    }

    @Override
    public Map<String, Object> getAgePreferenceDistribution(int itemId) {
        return this.sqlSession.selectOne(namespace + ".getAgePreferenceDistribution", itemId);
    }

    @Override
    public Map<String, Object> getGenderPreferenceDistribution(int itemId) {
        return this.sqlSession.selectOne(namespace + ".getGenderPreferenceDistribution", itemId);
    }

    @Override
    public void setAgeMaxPreferenceFlag(Map<String, Object> params) {
        this.sqlSession.update(namespace + ".setAgeMaxPreferenceFlag", params);
    }

    @Override
    public void setGenderMaxPreferenceFlag(Map<String, Object> params) {
        this.sqlSession.update(namespace + ".setGenderMaxPreferenceFlag", params);
    }

    @Override
    public void deleteCurrentPlaylist(@Param("store_id") String storeId) {
        this.sqlSession.delete(namespace + ".deleteCurrentPlaylist", storeId);
    }

    @Override
    public void insertCurrentSong(Map<String, Object> params) {
        this.sqlSession.insert(namespace + ".insertCurrentSong", params);
    }
}
