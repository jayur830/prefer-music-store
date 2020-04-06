package com.prefer_music_store.app.security;

import com.prefer_music_store.app.repo.UserAuthDAO;
import com.prefer_music_store.app.repo.UserDAO;
import com.prefer_music_store.app.repo.UserLogDAO;
import com.prefer_music_store.app.repo.UserVO;
import com.prefer_music_store.app.util.MapConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
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
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Resource(name = "userLogDAO")
    private UserLogDAO userLogDAO;
    @Resource(name = "userDAO")
    private UserDAO userDAO;
    @Resource(name = "userAuthDAO")
    private UserAuthDAO userAuthDAO;
    @Resource(name = "dateFormat")
    private DateFormat dateFormat;
    @Resource(name = "userTable")
    private Map<String, UserVO> userTable;

    @Transactional
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication auth) throws IOException, ServletException {
        request.getSession().removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

        // 로그인 페이지에 들어오기 전의 페이지 정보를 갖고 있는 객체
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        // 클라이언트에게 응답할 JSON 포맷을 생성하고, 유저가 로그인을 시도할 때 입력한 아이디를 얻는다.
        String json = "{ \"username\": \"%s\", \"targetUrl\": \"%s\", \"error\": null }", username = auth.getName();

        // 현재 로그인한 해당 유저의 로그인 시간을 DB에 반영한다.
        this.userLogDAO.setLoginDatetime(
                MapConverter.convertToHashMap(
                        new String[] { "username", "login_datetime" },
                        new Object[] { username, this.dateFormat.format(new Date()) }));

        // userTable: 현재 로그인한 유저들의 상태 정보를 모아놓은 테이블
        // 로그인에 성공하였으므로 해당 유저를 userTable에 추가한다.
        this.userTable.put(username, this.userDAO.getUserInfo(username));

        // 만약 로그인을 몇 번 실패하다가 성공한 경우라면
        if (this.userAuthDAO.getLoginFailureCount(username) != 0)
            // 로그인 실패 카운터를 0으로 초기화한다.
            // 이렇게 안하면 다음 번에 로그인할 때 로그인을 실패하는 횟수가 3번보다 적은 기회를 갖기 때문이다.
            this.userAuthDAO.initLoginFailureCount(username);

        // 로그인 페이지 이전에 접속했던 페이지가 있다면 그 페이지로 응답한다.
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            response.getWriter().print(String.format(json, username, targetUrl));
        }
        // 곧바로 로그인 페이지에서 직접 로그인하였다면 메인 페이지(index.jsp)로 응답한다.
        else response.getWriter().print(String.format(json, username, "/"));
    }
}
