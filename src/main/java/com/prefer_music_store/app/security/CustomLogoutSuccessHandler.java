package com.prefer_music_store.app.security;

import com.prefer_music_store.app.repo.UserDAO;
import com.prefer_music_store.app.repo.UserLogDAO;
import com.prefer_music_store.app.repo.UserVO;
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
        if (authentication != null) {
            Map<String, Object> params = new HashMap<>();
            String username = authentication.getName();
            params.put("username", username);
            params.put("logout_datetime", this.dateFormat.format(new Date()));
            this.userLogDAO.setLogoutDatetime(params);

            this.userTable.remove(username);

            response.sendRedirect("/login");
        }
    }
}
