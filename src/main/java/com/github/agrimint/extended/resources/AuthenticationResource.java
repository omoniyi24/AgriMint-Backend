package com.github.agrimint.extended.resources;

import com.github.agrimint.extended.dto.AccessTokenRequestDto;
import com.github.agrimint.extended.dto.JWTToken;
import com.github.agrimint.extended.service.ExtendedAppUserService;
import com.github.agrimint.extended.service.auth.AccessTokenService;
import com.github.agrimint.extended.service.auth.ExtendedRefreshTokenService;
import com.github.agrimint.security.SecurityUtils;
import com.github.agrimint.service.dto.AppUserDTO;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AuthenticationResource {

    private final AccessTokenService accessTokenService;

    private final ExtendedAppUserService extendedAppUserService;

    private final ExtendedRefreshTokenService extendedRefreshTokenService;

    @PostMapping(value = "user/login", produces = "application/json")
    public ResponseEntity<JWTToken> generateAccessToken(@RequestBody AccessTokenRequestDto accessTokenRequest) {
        JWTToken tokenInfo = null;

        Optional<AppUserDTO> userByPhoneNumberAndCountryCode = extendedAppUserService.findUserByPhoneNumberAndCountryCode(
            accessTokenRequest.getCountryCode(),
            accessTokenRequest.getPhoneNumber()
        );
        if (userByPhoneNumberAndCountryCode.isPresent()) {
            AppUserDTO profile = userByPhoneNumberAndCountryCode.get();
            if (!profile.getActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(tokenInfo);
            } else {
                try {
                    tokenInfo = accessTokenService.getJwtToken(profile, accessTokenRequest);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(tokenInfo);
                }
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(tokenInfo);
        }
        return ResponseEntity.ok(tokenInfo);
    }
    //    @PostMapping(value = "logout", produces = "application/json")
    //    public void logout(@RequestHeader("Authorization") String authorizationHeader) {
    //        try {
    //            String beaerToken = StringUtils.remove(authorizationHeader, "Bearer ");
    //            String login = SecurityUtils.getCurrentUserLogin().orElse(null);
    //            profileService.logout(login.toLowerCase(), beaerToken);
    //        } catch (Exception e) {
    //            log.error("Error logging user out", e);
    //        }
    //    }
}
