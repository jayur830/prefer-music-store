package com.prefer_music_store.app.security;

import com.prefer_music_store.app.repo.UserAuthDAO;
import com.prefer_music_store.app.util.MessageUtils;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Resource(name = "userAuthDAO")
    private UserAuthDAO userAuthDAO;

//    @Transactional
    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException e) throws IOException, ServletException {
        String errorMsg = e.getMessage();

        if (e instanceof BadCredentialsException) {
            String username = request.getParameter("username");
            if (this.userAuthDAO.getUserByUsername(username) != null) {
                this.userAuthDAO.updateLoginFailureCount(username);
                if (this.userAuthDAO.getLoginFailureCount(username) == 3) {
                    this.userAuthDAO.initLoginFailureCount(username);
                    this.userAuthDAO.disableAccount(username);
                }
            }
            errorMsg = MessageUtils.getMessage("error.BadCredentials");
        } else if (e instanceof CredentialsExpiredException) {
            errorMsg = MessageUtils.getMessage("error.CredentialsExpired");
        } else if (e instanceof DisabledException) {
            errorMsg = MessageUtils.getMessage("error.Disabled");
        } else if (e instanceof AccountExpiredException) {
            errorMsg = MessageUtils.getMessage("error.AccountExpired");
        } else if (e instanceof LockedException) {
            errorMsg = MessageUtils.getMessage("error.Locked");
        }

        response.setCharacterEncoding("utf-8");
        response.getWriter().print("{ \"username\": null, \"targetUrl\": null, \"error\": \"" + errorMsg + "\" }");
    }
}
