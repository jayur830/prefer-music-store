package com.prefer_music_store.app.security;

import com.prefer_music_store.app.repo.UserAuthDAO;
import com.prefer_music_store.app.util.EmailUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    @Resource(name = "userAuthDAO")
    private UserAuthDAO userAuthDAO;
    @Resource(name = "passwordEncoder")
    private PasswordEncoder passwordEncoder;
    @Resource(name = "emailUtils")
    private EmailUtils emailUtils;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = this.userAuthDAO.getUserByUsername(username);
        if (user == null) throw new UsernameNotFoundException(username);
        return user;
    }

    public boolean exist(String username) {
        return this.userAuthDAO.getUserByUsername(username) != null;
    }

    public void signUp(CustomUserDetails user) {
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        this.userAuthDAO.signUp(user);
    }

    public void resetPassword(Map<String, Object> params) {
        // 랜덤 문자열 8자리 생성 및 암호화
        String password = RandomStringUtils.randomAlphanumeric(8);
        String encodedPassword = this.passwordEncoder.encode(password);
        // DB 반영
        String email = (String) params.get("email");
        params.remove("email");
        params.put("password", encodedPassword);
        this.userAuthDAO.updatePassword(params);
        // 이메일 전송
        this.emailUtils.sendMail(email, "비밀번호 변경", "<div style=\"border: 7px solid blue; padding: 25px; text-align: center;\">회원님의 임시 비밀번호는 " + password + "입니다.</div>", true);
    }

    public void changePassword(String username, String password) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("password", this.passwordEncoder.encode(password));
        System.out.println("encoded password: " + password);
        System.out.println("decoded password: " + this.passwordEncoder.encode(password));
        this.userAuthDAO.updatePassword(params);
    }
}
