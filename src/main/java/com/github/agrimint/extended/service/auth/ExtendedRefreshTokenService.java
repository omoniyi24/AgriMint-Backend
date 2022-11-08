package com.github.agrimint.extended.service.auth;

import com.github.agrimint.extended.dto.RefreshTokenDTO;
import com.github.agrimint.security.jwt.TokenProvider;
import com.github.agrimint.service.dto.AppUserDTO;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExtendedRefreshTokenService {

    private final TokenProvider tokenProvider;

    @Value("${app.security.authentication.jwt.refresh-token-validity-in-seconds:300000}")
    private Long refreshTokenDurationMs;

    public RefreshTokenDTO createRefreshToken(AppUserDTO user) throws InvocationTargetException, IllegalAccessException {
        String token = tokenProvider.createRefreshToken(user, refreshTokenDurationMs);
        //        token = String.format("%s%s", user.getId(), UUID.randomUUID().toString());
        token = String.format("%s", UUID.randomUUID().toString());
        RefreshTokenDTO refreshToken = new RefreshTokenDTO();
        refreshToken.setAppUser(user);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(refreshTokenDurationMs));
        refreshToken.setToken(token);
        //        refreshToken = refreshTokenService.save(refreshToken);
        return refreshToken;
    }
}
