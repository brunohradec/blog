package me.bhradec.blog.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.bhradec.blog.domain.AppUser;
import me.bhradec.blog.service.AppUserService;
import me.bhradec.blog.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;
    private final AppUserService appUserService;

    public JwtAuthFilter(
            JwtService jwtService,
            AppUserService appUserService) {

        this.jwtService = jwtService;
        this.appUserService = appUserService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String jwt;

        // Get JWT from request auth header

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("Request auth header is null or does not start with \"Bearer \" prefix. Resuming filter chain.");
            filterChain.doFilter(request, response);
            return;
        }

        // JWT is in auth header after the "Bearer " prefix

        jwt = authHeader.substring(7);

        if (!jwtService.isTokenValid(jwt)) {
            log.debug("JWT {} is invalid. Resuming filter chain.", jwt);
            filterChain.doFilter(request, response);
            return;
        }

        // Save user from JWT subject to the Spring Security Context

        String jwtSubject = jwtService.extractSubject(jwt);
        Optional<AppUser> appUserOptional = appUserService.findByUsername(jwtSubject);

        if (appUserOptional.isEmpty()) {
            log.debug("User with username {} not found. Resuming filter chain.", jwtSubject);
            filterChain.doFilter(request, response);
            return;
        }

        AppUser appUser = appUserOptional.get();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                appUser,
                null,
                appUser.getAuthorities()
        );

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}

