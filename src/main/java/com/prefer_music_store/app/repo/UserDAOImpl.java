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
    public void signUp(UserDTO userDTO) {
        this.sqlSession.insert(namespace + ".signUp", userDTO);
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
    public UserDTO getUserInfo(@Param("username") String username) {
        return this.sqlSession.selectOne(namespace + ".getUserInfo", username);
    }

    @Override
    public void editUserInfo(UserDTO userDTO) {
        this.sqlSession.update(namespace + ".editUserInfo", userDTO);
    }

    @Override
    public void deleteUserInfo(@Param("username") String username) {
        this.sqlSession.delete(namespace + ".deleteUserInfo", username);
    }

    @Override
    public List<String> getUsers() {
        return this.sqlSession.selectList(namespace + ".getUsers");
    }
}
