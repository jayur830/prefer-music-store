package com.prefer_music_store.app.repo;

import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository("storeDAO")
public class StoreDAOImpl implements StoreDAO {
    @Resource(name = "sqlSession")
    private SqlSessionTemplate sqlSession;

    private static String namespace = "mappers.StoreMapper";

    @Override
    public String getStoreId(@Param("username") String username) {
        return this.sqlSession.selectOne(namespace + ".getStoreId", username);
    }
}