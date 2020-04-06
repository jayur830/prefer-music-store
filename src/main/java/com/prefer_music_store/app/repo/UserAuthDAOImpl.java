package com.prefer_music_store.app.repo;

import com.prefer_music_store.app.security.CustomUserDetails;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;

@Repository("userAuthDAO")
public class UserAuthDAOImpl implements UserAuthDAO {
    @Resource(name = "sqlSession")
    private SqlSessionTemplate sqlSession;

    private static String namespace = "mappers.UserAuthMapper";

    @Override
    public void signUp(CustomUserDetails user) {
        this.sqlSession.insert(namespace + ".signUp", user);
    }

    @Override
    public CustomUserDetails getUserByUsername(@Param("username") String username) {
        return this.sqlSession.selectOne(namespace + ".getUserByUsername", username);
    }

    @Override
    public void updateLoginFailureCount(@Param("username") String username) {
        this.sqlSession.update(namespace + ".updateLoginFailureCount", username);
    }

    @Override
    public int getLoginFailureCount(@Param("username") String username) {
        return this.sqlSession.selectOne(namespace + ".getLoginFailureCount", username);
    }

    @Override
    public void disableAccount(@Param("username") String username) {
        this.sqlSession.update(namespace + ".disableAccount", username);
    }

    @Override
    public void initLoginFailureCount(@Param("username") String username) {
        this.sqlSession.update(namespace + ".initLoginFailureCount", username);
    }

    @Override
    public void updatePassword(Map<String, Object> params) {
        this.sqlSession.update(namespace + ".updatePassword", params);
    }
}
