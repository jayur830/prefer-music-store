package com.prefer_music_store.app.repo;

import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository("ratingDAO")
public class UserRatingDAOImpl implements UserRatingDAO {
    @Resource(name = "sqlSession")
    private SqlSessionTemplate sqlSession;

    private static String namespace = "mappers.UserRatingMapper";

    @Override
    public boolean isRated(Map<String, Object> params) {
        return !this.sqlSession.selectOne(namespace + ".isRated", params).equals(0);
    }

    @Override
    public void insertRating(Map<String, Object> params) {
        this.sqlSession.insert(namespace + ".insertRating", params);
    }

    @Override
    public void updateRating(Map<String, Object> params) {
        this.sqlSession.update(namespace + ".updateRating", params);
    }

    @Override
    public void deleteUserRating(@Param("user_id") String userId) {
        this.sqlSession.delete(namespace + ".deleteUserRating", userId);
    }

    @Override
    public List<Map<String, Object>> getRatings() {
        return this.sqlSession.selectList(namespace + ".getRatings");
    }

    @Override
    public List<Map<String, Object>> getAvgRatingsGroupByAge() {
        return this.sqlSession.selectList(namespace + ".getAvgRatingsGroupByAge");
    }

    @Override
    public List<Map<String, Object>> getAvgRatingsGroupByGender() {
        return this.sqlSession.selectList(namespace + ".getAvgRatingsGroupByGender");
    }
}
