package com.prefer_music_store.app.security;

import com.prefer_music_store.app.repo.UserDAO;
import com.prefer_music_store.app.repo.UserLogDAO;
import com.prefer_music_store.app.repo.UserVO;
import com.prefer_music_store.app.util.MapConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    @Resource(name = "userLogDAO")
    private UserLogDAO userLogDAO;
    @Resource(name = "dateFormat")
    private DateFormat dateFormat;
    @Resource(name = "userTable")
    private Map<String, UserVO> userTable;

    @Transactional
    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        // 유효한 계정의 유저가 해킹 등의 방법이 아닌 정상적으로 로그아웃을 하는 경우
        if (authentication != null) {
            // 해당 계정의 아이디
            String username = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().toArray()[0].toString().equals("ROLE_ADMIN");

            if (!isAdmin) {
                // 해당 계정의 로그아웃 시간을 DB에 반영한다.
                this.userLogDAO.setLogoutDatetime(
                        MapConverter.convertToHashMap(
                                new String[]{"username", "logout_datetime"},
                                new Object[]{username, this.dateFormat.format(new Date())}));

                // 로그아웃 하였으므로 현재 로그인한 유저들의 정보를 모아놓은 테이블에서도 해당 계정 정보를 지운다.
                this.userTable.remove(username);
            }

            // 클라이언트에게 로그인 페이지로 응답한다.
            response.sendRedirect("/login");
        }
    }
}
