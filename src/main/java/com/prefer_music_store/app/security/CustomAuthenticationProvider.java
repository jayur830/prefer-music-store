package com.prefer_music_store.app.security;

import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Component("authenticationProvider")
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Resource(name = "passwordEncoder")
    private PasswordEncoder passwordEncoder;
    @Resource(name = "userDetailsService")
    private CustomUserDetailsService userDetailsService;

    @Transactional
    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String username = (String) auth.getPrincipal(), password = (String) auth.getCredentials();
        //
        UserDetails user = this.userDetailsService.loadUserByUsername(username);
        password = this.passwordEncoder.encode(password);

        // 아이디 또는 비밀번호가 일치하지 않는 경우
        if (!user.getUsername().equals(username) ||
                !user.getPassword().equals(password))
            throw new BadCredentialsException(username);
        // 비밀번호 유효기간이 만료된 경우
        else if (!user.isCredentialsNonExpired()) throw new CredentialsExpiredException(username);
        // 해당 계정이 비활성화된 경우
        else if (!user.isEnabled()) throw new DisabledException(username);
        // 해당 계정이 만료된 경우
        else if (!user.isAccountNonExpired()) throw new AccountExpiredException(username);
        // 해당 계정이 잠긴 경우
        else if (!user.isAccountNonLocked()) throw new LockedException(username);

        // 아이디, 비밀번호, 권한 정보에 대한 인증 토큰 반환                                                                                                                                                                                                                                                                        이라고는 써놨는데 솔직히 나도 잘 모름
        return new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
