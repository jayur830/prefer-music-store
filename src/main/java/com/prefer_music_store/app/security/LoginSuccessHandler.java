package com.prefer_music_store.app.security;

import com.prefer_music_store.app.repo.UserAuthDAO;
import com.prefer_music_store.app.repo.UserDAO;
import com.prefer_music_store.app.repo.UserLogDAO;
import com.prefer_music_store.app.repo.UserVO;
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

        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        String json = "{ \"username\": \"%s\", \"targetUrl\": \"%s\", \"error\": null }", username = auth.getName();

        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("login_datetime", this.dateFormat.format(new Date()));
        this.userLogDAO.setLoginDatetime(params);

        this.userTable.put(username, this.userDAO.getUserInfo(username));

        if (this.userAuthDAO.getLoginFailureCount(username) != 0)
            this.userAuthDAO.initLoginFailureCount(username);

        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            response.getWriter().print(String.format(json, username, targetUrl));
        } else response.getWriter().print(String.format(json, username, "/"));
    }
}
