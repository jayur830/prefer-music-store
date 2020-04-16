package com.prefer_music_store.app.service;

import com.prefer_music_store.app.repo.*;
import com.prefer_music_store.app.util.MapConverter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Resource(name = "userAuthDAO")
    private UserAuthDAO userAuthDAO;
    @Resource(name = "userDAO")
    private UserDAO userDAO;
    @Resource(name = "userLogDAO")
    private UserLogDAO userLogDAO;
    @Resource(name = "ratingDAO")
    private UserRatingDAO ratingDAO;
    @Resource(name = "storeDAO")
    private StoreDAO storeDAO;

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
    public void deleteUserInfo(String username) {
        // user 테이블에서 삭제
        this.userDAO.deleteUserInfo(username);
        // user_auth 테이블에서 삭제
        this.userAuthDAO.deleteUserAuthInfo(username);
        // user_log 테이블에서 삭제
        this.userLogDAO.deleteUserLog(username);
        // user_rating 테이블에서 삭제
        this.ratingDAO.deleteUserRating(username);
    }

    @Override
    public void updateRatingHistory(String userId, String ratingDatetime) {
        // userId에 해당하는 유저의 최근 평점 반영 시간을 ratingDatetime으로 갱신한다.
        this.userDAO.updateRatingHistory(
                MapConverter.convertToHashMap(
                        new String[] { "user_id", "rating_datetime" },
                        new Object[] { userId, ratingDatetime }));
    }

    @Override
    public String getStoreId(String username) {
        return this.storeDAO.getStoreId(username);
    }
}
