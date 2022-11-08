package com.github.agrimint.extended.service.auth;

import com.github.agrimint.extended.dto.AccessTokenRequestDto;
import com.github.agrimint.extended.dto.JWTToken;
import com.github.agrimint.extended.dto.JwtAppUserDTO;
import com.github.agrimint.security.jwt.TokenProvider;
import com.github.agrimint.service.dto.AppUserDTO;
import java.lang.reflect.InvocationTargetException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccessTokenService {

    private final TokenProvider tokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final ExtendedRefreshTokenService extendedRefreshTokenService;

    @Transactional(readOnly = true)
    public String getBearerToken(JwtAppUserDTO userDTO, AccessTokenRequestDto accessTokenRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            accessTokenRequest.getCountryCode() + accessTokenRequest.getPhoneNumber(),
            accessTokenRequest.getSecret()
        );
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        if (authentication.isAuthenticated()) {
            String jwt = tokenProvider.createToken(userDTO, authentication.getAuthorities(), Boolean.TRUE);
            if (StringUtils.isNoneBlank(jwt)) {
                return jwt;
            } else {
                return null;
            }
        }
        return null;
    }

    public String getBearerTokenForUser(AppUserDTO appUser) throws Exception {
        String jwt = tokenProvider.createToken(appUser, Boolean.TRUE);
        if (StringUtils.isNoneBlank(jwt)) {
            return jwt;
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public String getRefreshToken(JwtAppUserDTO jwtUserInfo, AppUserDTO profile) throws InvocationTargetException, IllegalAccessException {
        BeanUtils.copyProperties(profile, jwtUserInfo);
        return extendedRefreshTokenService.createRefreshToken(profile).getToken();
    }

    public JWTToken getJwtToken(AppUserDTO profile, AccessTokenRequestDto accessTokenRequest) throws Exception {
        JwtAppUserDTO jwtUserInfo = new JwtAppUserDTO();
        BeanUtils.copyProperties(profile, jwtUserInfo);
        String accessToken = getBearerToken(jwtUserInfo, accessTokenRequest);
        String refreshToken = getRefreshToken(jwtUserInfo, profile);
        return new JWTToken(accessToken, refreshToken);
    }
}
