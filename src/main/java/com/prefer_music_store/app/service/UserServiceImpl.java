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
    @Resource(name = "ratingDAO")
    private UserRatingDAO ratingDAO;
    @Resource(name = "storeDAO")
    private StoreDAO storeDAO;

    @Override
    public void signUp(UserDTO userDTO) {
        this.userDAO.signUp(userDTO);
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
    public UserDTO getUserInfo(String username) {
        return this.userDAO.getUserInfo(username);
    }

    @Override
    public void editUserInfo(UserDTO userDTO) {
        this.userDAO.editUserInfo(userDTO);
    }

    @Override
    public void deleteUserInfo(String username) {
        // user 테이블에서 삭제
        this.userDAO.deleteUserInfo(username);
        // user_auth 테이블에서 삭제
        this.userAuthDAO.deleteUserAuthInfo(username);
        // user_rating 테이블에서 삭제
        this.ratingDAO.deleteUserRating(username);
    }

    @Override
    public String getStoreId(String username) {
        return this.storeDAO.getStoreId(username);
    }

    @Override
    public void rating(String userId, String itemId, double rating) {
        // 평점 반영 이력 데이터
        Map<String, Object> params = MapConverter.convertToHashMap(
                new String[] { "user_id", "item_id", "rating" },
                new Object[] { userId, Integer.parseInt(itemId), rating });

        // 해당 유저가 해당 곡에 대해 이전에 평점을 반영한 이력이 존재한다면
        if (this.ratingDAO.isRated(params))
            // 기존의 이력을 업데이트하고
            this.ratingDAO.updateRating(params);
            // 그렇지 않으면 평점 반영 이력 데이터를 새로 추가한다.
        else this.ratingDAO.insertRating(params);
    }
}
