package com.prefer_music_store.app.security;

import com.prefer_music_store.app.repo.UserAuthDAO;
import com.prefer_music_store.app.repo.UserDAO;
import com.prefer_music_store.app.repo.UserVO;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Resource(name = "userDAO")
    private UserDAO userDAO;
    @Resource(name = "userAuthDAO")
    private UserAuthDAO userAuthDAO;
    @Resource(name = "userTable")
    private Map<String, UserVO> userTable;

    @Transactional
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication auth) throws IOException, ServletException {
        request.getSession().removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

        // 클라이언트에게 응답할 JSON 포맷을 생성하고, 유저가 로그인을 시도할 때 입력한 아이디를 얻는다.
        String json = "{ \"username\": \"%s\", \"targetUrl\": \"%s\", \"error\": null }", username = auth.getName();
        boolean isAdmin = auth.getAuthorities().toArray()[0].toString().equals("ROLE_ADMIN");

        if (!isAdmin) {
            // userTable: 현재 로그인한 유저들의 상태 정보를 모아놓은 테이블
            // 로그인에 성공하였으므로 해당 유저를 userTable에 추가한다.
            this.userTable.put(username, this.userDAO.getUserInfo(username));
        }

        // 만약 로그인을 몇 번 실패하다가 성공한 경우라면
        if (this.userAuthDAO.getLoginFailureCount(username) != 0)
            // 로그인 실패 카운터를 0으로 초기화한다.
            // 이렇게 안하면 다음 번에 로그인할 때 로그인을 실패하는 횟수가 3번보다 적은 기회를 갖기 때문이다.
            this.userAuthDAO.initLoginFailureCount(username);

        // 곧바로 로그인 페이지에서 직접 로그인하였다면,
        // 그리고 관리자 권한을 갖고 있다면 admin 페이지(admin.jsp)로,
        // 일반 유저일 경우 메인 페이지(main.jsp)로 응답한다.
        if (isAdmin) response.getWriter().print(String.format(json, username, "/admin"));
        else response.getWriter().print(String.format(json, username, "/main"));
    }
}
