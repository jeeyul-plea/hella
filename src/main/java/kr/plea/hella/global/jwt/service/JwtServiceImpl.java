package kr.plea.hella.global.jwt.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.plea.hella.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService{

    private final RefreshTokenService refreshTokenService;
    private final MemberRepository memberRepository;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String USERNAME_CLAIM = "username";
    private static final String BEARER = "Bearer ";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiration}")
    private long accessTokenValidityInSeconds;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenValidityInSeconds;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;


    @Override
    public String createAccessToken(String username) {
        return JWT.create()
            .withSubject(ACCESS_TOKEN_SUBJECT)
            .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenValidityInSeconds * 1000))
            .withClaim(USERNAME_CLAIM, username)
            .sign(Algorithm.HMAC512(secret));
    }

    @Override
    public String createRefreshToken(String username) {
        return JWT.create()
            .withSubject(REFRESH_TOKEN_SUBJECT)
            .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenValidityInSeconds * 1000))
            .withClaim(USERNAME_CLAIM, username)
            .sign(Algorithm.HMAC512(secret));
    }

    @Transactional
    @Override
    public void updateRefreshToken(String username, String refreshToken) {
        memberRepository.findByUsername(username)
            .ifPresentOrElse(
                m -> refreshTokenService.setRefreshToken(username, refreshToken),
                () -> new Exception("회원이 없습니다")
            );
    }

    @Transactional
    @Override
    public void destroyRefreshToken(String username) {
        memberRepository.findByUsername(username)
            .ifPresentOrElse(
                m -> refreshTokenService.deleteRefreshToken(username),
                () -> new Exception("회원이 없습니다")
            );
    }

    @Override
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken){
        response.setStatus(HttpServletResponse.SC_OK);
        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);
    }

    @Override
    public void sendAccessToken(HttpServletResponse response, String accessToken){
        response.setStatus(HttpServletResponse.SC_OK);
        setAccessTokenHeader(response, accessToken);
    }



    @Override
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader)).filter(

            accessToken -> accessToken.startsWith(BEARER)

        ).map(accessToken -> accessToken.replace(BEARER, ""));
    }

    @Override
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader)).filter(

            refreshToken -> refreshToken.startsWith(BEARER)

        ).map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    @Override
    public Optional<String> extractUsername(String token) {
        try {
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secret)).build().verify(token).getClaim(USERNAME_CLAIM).asString());
        }catch (Exception e){
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }

    @Override
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
    }
    @Override
    public boolean isTokenValid(String token){
        try {
            JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
            return true;
        }catch (Exception e){
            log.error("유효하지 않은 Token입니다", e.getMessage());
            return false;
        }
    }
}
