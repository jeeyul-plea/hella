package kr.plea.hella.global.login.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.plea.hella.domain.member.entity.Member;
import kr.plea.hella.domain.member.repository.MemberRepository;
import kr.plea.hella.global.jwt.service.JwtService;
import kr.plea.hella.global.jwt.service.RefreshTokenService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final RefreshTokenService refreshTokenService;
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();//5

    /**
     * 1. 리프레시 토큰이 오는 경우 -> 유효하면 AccessToken 재발급후, 필터 진행 X, 바로 튕기기
     *
     * 2. 리프레시 토큰은 없고 AccessToken만 있는 경우 -> 유저정보 저장후 필터 계속 진행
     *
     * 3. 리프레시 토큰이 만료된 경우 다시 로그인해서 새로 발급
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain) throws
        ServletException, IOException {
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        boolean postValid =
            (method.equals("POST") || method.equals("PATCH") || method.equals("DELETE")) && requestUri.startsWith(
                "/posts");
        boolean postWithMember = method.equals("GET") && requestUri.equals("/posts/members");
        boolean memberValid = requestUri.startsWith("/members");
        boolean commentValid = requestUri.startsWith("/comments");
        boolean memberEdgeCase = method.equals("POST") && requestUri.equals("/members/signup");
        if (postValid || memberValid || postWithMember || commentValid) {
            if (memberEdgeCase) {
                filterChain.doFilter(request, response);
                return;
            }
            String refreshToken = jwtService
                .extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);
            if (refreshToken != null) {
                checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
                return;
            }
            checkAccessTokenAndAuthentication(request, response, filterChain);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException,
        IOException {
        jwtService.extractAccessToken(request)
            .filter(jwtService::isTokenValid)
            .flatMap(jwtService::extractUsername)
            .flatMap(memberRepository::findByUsername)
            .ifPresent(this::saveAuthentication);
        filterChain.doFilter(request, response);
    }

    private void saveAuthentication(Member member) {
        UserDetails user = User.builder()
            .username(member.getUsername())
            .password(member.getPassword())
            .roles(member.getRole().name())
            .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,
            authoritiesMapper.mapAuthorities(user.getAuthorities()));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        String username = jwtService.extractUsername(refreshToken).orElse("");
        if (!username.isEmpty()) {
            String findToken = refreshTokenService.getRefreshToken(username);
            if (findToken.equals(refreshToken)) {
                jwtService.sendAccessToken(response, jwtService.createAccessToken(username));
            }
        }
    }
}