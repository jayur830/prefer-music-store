package com.prefer_music_store.app.repo;

import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository("userDAO")
public class UserDAOImpl implements UserDAO {
    @Resource(name = "sqlSession")
    private SqlSessionTemplate sqlSession;

    private static String namespace = "mappers.UserMapper";

    @Override
    public void signUp(UserVO userVO) {
        this.sqlSession.insert(namespace + ".signUp", userVO);
    }

    @Override
    public String findUsername(Map<String, Object> params) {
        return this.sqlSession.selectOne(namespace + ".findUsername", params);
    }

    @Override
    public boolean existUserByUsernameAndEmail(Map<String, Object> params) {
        return !this.sqlSession.selectOne(namespace + ".existUserByUsernameAndEmail", params).equals(0);
    }

    @Override
    public UserVO getUserInfo(@Param("username") String username) {
        return this.sqlSession.selectOne(namespace + ".getUserInfo", username);
    }

    @Override
    public void editUserInfo(UserVO userVO) {
        this.sqlSession.update(namespace + ".editUserInfo", userVO);
    }

    @Override
    public void deleteUserInfo(@Param("username") String username) {
        this.sqlSession.delete(namespace + ".deleteUserInfo", username);
    }

    @Override
    public List<String> getUsers() {
        return this.sqlSession.selectList(namespace + ".getUsers");
    }

    @Override
    public Map<String, Object> getRatingHistory(@Param("username") String username) {
        return this.sqlSession.selectOne(namespace + ".getRatingHistory", username);
    }

    @Override
    public void updateRatingHistory(Map<String, Object> params) {
        this.sqlSession.update(namespace + ".updateRatingHistory", params);
    }
}
