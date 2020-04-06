package com.prefer_music_store.app.security;

import com.prefer_music_store.app.repo.UserAuthDAO;
import com.prefer_music_store.app.util.EmailUtils;
import com.prefer_music_store.app.util.MapConverter;
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
        // username의 아이디에 대한 회원 인증 정보를 가져온다.
        UserDetails user = this.userAuthDAO.getUserByUsername(username);
        // 정보가 없다면 아이디를 못찾았다는 예외를 던진다.
        if (user == null) throw new UsernameNotFoundException(username);
        return user;
    }

    public boolean exist(String username) {
        return this.userAuthDAO.getUserByUsername(username) != null;
    }

    public void signUp(CustomUserDetails user) {
        // DB에 회원 인증 정보를 반영하기 전에 먼저 비밀번호를 암호화한다.
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        // 해당 회원의 인증 정보를 DB에 반영한다.
        this.userAuthDAO.signUp(user);
    }

    public void resetPassword(Map<String, Object> params) {
        // 랜덤 문자열 8자리를 생성하고
        String password = RandomStringUtils.randomAlphanumeric(8);
        // 그것을 암호화한 문자열을 생성한다.
        String encodedPassword = this.passwordEncoder.encode(password);

        // 이메일 데이터를 가져오고
        String email = (String) params.get("email");
        // 해당 맵 객체에서 이메일을 제거한다.
        params.remove("email");
        // 암호화된 임시 비밀번호 문자열을 추가한 뒤
        params.put("password", encodedPassword);
        // DB에 반영한다.
        this.userAuthDAO.updatePassword(params);

        // 해당 이메일 주소로 새롭게 초기화 된 임시 비밀번호를 이메일로 전송한다.
        this.emailUtils.sendMail(email, "비밀번호 변경", "<div style=\"border: 7px solid blue; padding: 25px; text-align: center;\">회원님의 임시 비밀번호는 " + password + "입니다.</div>", true);
    }

    public void changePassword(String username, String password) {
        // username 계정의 비밀번호를 password로 변경하고 그것을 DB에 반영한다.
        this.userAuthDAO.updatePassword(
                MapConverter.convertToHashMap(
                        new String[] { "username", "password" },
                        new Object[] { username, this.passwordEncoder.encode(password) }));
    }
}
