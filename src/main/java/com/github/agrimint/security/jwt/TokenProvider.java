package com.github.agrimint.security.jwt;

import static tech.jhipster.config.JHipsterDefaults.Security.Authentication.Jwt.tokenValidityInSeconds;
import static tech.jhipster.config.JHipsterDefaults.Security.Authentication.Jwt.tokenValidityInSecondsForRememberMe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.agrimint.extended.dto.JwtAppUserDTO;
import com.github.agrimint.service.dto.AppUserDTO;
import com.github.agrimint.service.dto.RoleDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import tech.jhipster.config.JHipsterProperties;

@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String INVALID_JWT_TOKEN = "Invalid JWT token.";
    private final Logger log = LoggerFactory.getLogger(TokenProvider.class);
    private final Key key;

    private final JwtParser jwtParser;

    private final long tokenValidityInMilliseconds;

    private final long tokenValidityInMillisecondsForRememberMe;

    private final SecurityMetersService securityMetersService;

    @Autowired
    ObjectMapper objectMapper;

    public TokenProvider(JHipsterProperties jHipsterProperties, SecurityMetersService securityMetersService) {
        byte[] keyBytes;
        String secret = jHipsterProperties.getSecurity().getAuthentication().getJwt().getBase64Secret();
        if (!ObjectUtils.isEmpty(secret)) {
            log.debug("Using a Base64-encoded JWT secret key");
            keyBytes = Decoders.BASE64.decode(secret);
        } else {
            log.warn(
                "Warning: the JWT key used is not Base64-encoded. " +
                "We recommend using the `jhipster.security.authentication.jwt.base64-secret` key for optimum security."
            );
            secret = jHipsterProperties.getSecurity().getAuthentication().getJwt().getSecret();
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        key = Keys.hmacShaKeyFor(keyBytes);
        jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
        this.tokenValidityInMilliseconds = 1000 * jHipsterProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSeconds();
        this.tokenValidityInMillisecondsForRememberMe =
            1000 * jHipsterProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSecondsForRememberMe();

        this.securityMetersService = securityMetersService;
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        return createToken(null, authentication.getAuthorities(), rememberMe);
    }

    public String createRefreshToken(AppUserDTO profile, long expires) throws InvocationTargetException, IllegalAccessException {
        String authorities = profile.getAuthorities().stream().map(RoleDTO::getName).collect(Collectors.joining(","));
        Date issuedDate = new Date();
        long now = (issuedDate).getTime();
        ZonedDateTime zdtNow = ZonedDateTime.now(ZoneId.systemDefault());

        Date validity;
        validity = Date.from(zdtNow.plusSeconds(expires).toInstant());

        JwtAppUserDTO jwtUserInfo = new JwtAppUserDTO();
        BeanUtils.copyProperties(jwtUserInfo, profile);
        return Jwts
            .builder()
            .serializeToJsonWith(new JacksonSerializer(objectMapper))
            .setIssuer("AgriMint")
            .setIssuedAt(issuedDate)
            .setSubject(profile.getName())
            .claim(AUTHORITIES_KEY, authorities)
            .claim("user", jwtUserInfo)
            .claim("ttype", "Bearer")
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(validity)
            .compact();
    }

    public String createToken(AppUserDTO profile, boolean rememberMe) throws InvocationTargetException, IllegalAccessException {
        String authorities = profile.getAuthorities().stream().map(RoleDTO::getName).collect(Collectors.joining(","));
        Date issuedDate = new Date();
        long now = (issuedDate).getTime();
        ZonedDateTime zdtNow = ZonedDateTime.now(ZoneId.systemDefault());

        Date validity;
        if (rememberMe) {
            validity = Date.from(zdtNow.plusSeconds(tokenValidityInSecondsForRememberMe).toInstant());
        } else {
            validity = Date.from(zdtNow.plusSeconds(tokenValidityInSeconds).toInstant());
        }
        JwtAppUserDTO jwtUserInfo = new JwtAppUserDTO();
        BeanUtils.copyProperties(jwtUserInfo, profile);
        return Jwts
            .builder()
            .serializeToJsonWith(new JacksonSerializer(objectMapper))
            .setIssuer("AgriMint")
            .setIssuedAt(issuedDate)
            .setSubject(profile.getName())
            .claim(AUTHORITIES_KEY, authorities)
            .claim("user", jwtUserInfo)
            .claim("ttype", "Bearer")
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(validity)
            .compact();
    }

    public String createToken(JwtAppUserDTO profile, Collection<? extends GrantedAuthority> grantedAuths, boolean rememberMe) {
        String authorities = grantedAuths.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        Date issuedDate = new Date();

        ZonedDateTime zdtNow = ZonedDateTime.now(ZoneId.systemDefault());

        Date validity;
        if (rememberMe) {
            validity = Date.from(zdtNow.plusSeconds(tokenValidityInSecondsForRememberMe).toInstant());
        } else {
            validity = Date.from(zdtNow.plusSeconds(tokenValidityInSeconds).toInstant());
        }

        return Jwts
            .builder()
            .serializeToJsonWith(new JacksonSerializer(objectMapper))
            .setIssuer("AgriMint")
            .setIssuedAt(issuedDate)
            .setSubject(profile.getCountryCode() + profile.getPhoneNumber())
            .claim(AUTHORITIES_KEY, authorities)
            .claim("user", profile)
            .claim("ttype", "Bearer")
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(validity)
            .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = jwtParser.parseClaimsJws(token).getBody();

        Collection<? extends GrantedAuthority> authorities = Arrays
            .stream(claims.get(AUTHORITIES_KEY).toString().split(","))
            .filter(auth -> !auth.trim().isEmpty())
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String authToken) {
        try {
            jwtParser.parseClaimsJws(authToken);

            return true;
        } catch (ExpiredJwtException e) {
            this.securityMetersService.trackTokenExpired();

            log.trace(INVALID_JWT_TOKEN, e);
        } catch (UnsupportedJwtException e) {
            this.securityMetersService.trackTokenUnsupported();

            log.trace(INVALID_JWT_TOKEN, e);
        } catch (MalformedJwtException e) {
            this.securityMetersService.trackTokenMalformed();

            log.trace(INVALID_JWT_TOKEN, e);
        } catch (SignatureException e) {
            this.securityMetersService.trackTokenInvalidSignature();

            log.trace(INVALID_JWT_TOKEN, e);
        } catch (IllegalArgumentException e) { // TODO: should we let it bubble (no catch), to avoid defensive programming and follow the fail-fast principle?
            log.error("Token validation error {}", e.getMessage());
        }

        return false;
    }
}
