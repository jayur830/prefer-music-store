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
        // 로그인 실패에 대한 에러 메시지
        String errorMsg = e.getMessage();

        /**
         * 이것에 대한 메시지 내용들은 web/META-INF/properties/security-message.properties 에서 확인할 수 있다.
         * */

        // 아이디 또는 비밀번호를 잘못 입력한 경우
        if (e instanceof BadCredentialsException) {
            // 로그인 시도 시 유저가 입력한 아이디
            String username = request.getParameter("username");

            // 해당 아이디에 대한 계정이 존재한다면
            if (this.userAuthDAO.getUserByUsername(username) != null) {
                // 해당 계정의 로그인 실패 카운터를 1 증가시킨다.
                this.userAuthDAO.updateLoginFailureCount(username);

                // 해당 계정의 로그인 실패 카운터가 3이 되어버린 경우
                if (this.userAuthDAO.getLoginFailureCount(username) == 3) {
                    // 로그인 실패 카운터를 0으로 초기화시키고
                    this.userAuthDAO.initLoginFailureCount(username);
                    // 해당 계정을 비활성화시켜버린다.
                    this.userAuthDAO.disableAccount(username);
                }
            }
            errorMsg = MessageUtils.getMessage("error.BadCredentials");
        }
        // 비밀번호 유효기간이 만료된 경우
        else if (e instanceof CredentialsExpiredException)
            errorMsg = MessageUtils.getMessage("error.CredentialsExpired");
        // 해당 계정이 비활성화된 경우
        else if (e instanceof DisabledException)
            errorMsg = MessageUtils.getMessage("error.Disabled");
        // 해당 계정이 만료된 경우
        else if (e instanceof AccountExpiredException)
            errorMsg = MessageUtils.getMessage("error.AccountExpired");
        // 해당 계정이 잠긴 경우
        else if (e instanceof LockedException)
            errorMsg = MessageUtils.getMessage("error.Locked");

        // 한글창제
        response.setCharacterEncoding("utf-8");
        // 에러 메시지만 담긴 JSON 객체를 클라이언트에 응답
        response.getWriter().print("{ \"username\": null, \"targetUrl\": null, \"error\": \"" + errorMsg + "\" }");
    }
}
