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
        UserDetails user = this.userDetailsService.loadUserByUsername(username);
        password = this.passwordEncoder.encode(password);

        if (!user.getUsername().equals(username) ||
                !user.getPassword().equals(password))
            throw new BadCredentialsException(username);
        else if (!user.isCredentialsNonExpired()) throw new CredentialsExpiredException(username);
        else if (!user.isEnabled()) throw new DisabledException(username);
        else if (!user.isAccountNonExpired()) throw new AccountExpiredException(username);
        else if (!user.isAccountNonLocked()) throw new LockedException(username);

        return new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
