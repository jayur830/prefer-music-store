package com.prefer_music_store.app.service;

import com.prefer_music_store.app.repo.UserDAO;
import com.prefer_music_store.app.repo.UserVO;
import com.prefer_music_store.app.util.EmailUtils;
import com.prefer_music_store.app.util.MapConverter;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Resource(name = "userDAO")
    private UserDAO userDAO;

    @Override
    public void signUp(UserVO userVO) {
        this.userDAO.signUp(userVO);
    }

    @Override
    public String findUsername(Map<String, Object> params) {
        return this.userDAO.findUsername(params);
    }

    @Override
    public boolean existUserByUsernameAndEmail(Map<String, Object> params) {
        return this.userDAO.existUserByUsernameAndEmail(params);
    }

    @Override
    public UserVO getUserInfo(String username) {
        return this.userDAO.getUserInfo(username);
    }

    @Override
    public void editUserInfo(UserVO userVO) {
        this.userDAO.editUserInfo(userVO);
    }

    @Override
    public void updateRatingHistory(String userId, String ratingDatetime) {
        // userId에 해당하는 유저의 최근 평점 반영 시간을 ratingDatetime으로 갱신한다.
        this.userDAO.updateRatingHistory(
                MapConverter.convertToHashMap(
                        new String[] { "user_id", "rating_datetime" },
                        new Object[] { userId, ratingDatetime }));
    }
}
