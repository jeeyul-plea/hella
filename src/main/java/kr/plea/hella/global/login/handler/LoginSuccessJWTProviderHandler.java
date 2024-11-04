package kr.plea.hella.global.login.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.plea.hella.global.jwt.service.JwtService;
import kr.plea.hella.global.jwt.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessJWTProviderHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws
        IOException, ServletException {
        String username = extractUsername(authentication);
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken(username);

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        refreshTokenService.setRefreshToken(username, refreshToken);

        log.debug( "AccessToken 을 발급합니다. AccessToken: {}" ,accessToken);
        log.debug( "RefreshToken 을 발급합니다. RefreshToken: {}" ,refreshToken);

        response.getWriter().write("login: success");
    }

    private String extractUsername(Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
