package com.prefer_music_store.app.repo;

import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository("userLogDAO")
public class UserLogDAOImpl implements UserLogDAO {
    @Resource(name = "sqlSession")
    private SqlSessionTemplate sqlSession;

    private static String namespace = "mappers.UserLogMapper";

    @Override
    public void setLoginDatetime(Map<String, Object> params) {
        this.sqlSession.insert(namespace + ".setLoginDatetime", params);
    }

    @Override
    public void setLogoutDatetime(Map<String, Object> params) {
        this.sqlSession.update(namespace + ".setLogoutDatetime", params);
    }

    @Override
    public List<String> getCurrentLoginUsers() {
        return this.sqlSession.selectList(namespace + ".getCurrentLoginUsers");
    }

    @Override
    public double getAvgActiveTime(@Param("user_id") String userId) {
        Object result = this.sqlSession.selectOne(namespace + ".getAvgActiveTime", userId);
        return result == null ? 0 : (double) result;
    }
}
