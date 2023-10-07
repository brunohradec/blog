package me.bhradec.blog.service;

import me.bhradec.blog.domain.AppUser;
import me.bhradec.blog.dto.LoginCommandDto;
import me.bhradec.blog.dto.LoginDto;
import me.bhradec.blog.exception.NotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final AppUserService appUserService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            AppUserService appUserService,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {

        this.appUserService = appUserService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public Optional<AppUser> getCurrentUser() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return appUserService.findByUsername(username);
    }

    public LoginDto login(LoginCommandDto loginCommandDto) throws NotFoundException {
        AppUser appUser = appUserService
                .findByUsername(loginCommandDto.getUsername())
                .orElseThrow(() -> new NotFoundException("User with username " + loginCommandDto.getUsername() + " not found"));

        // Will throw BadCredentialsException if the username or password is incorrect
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginCommandDto.getUsername(),
                        loginCommandDto.getPassword()
                )
        );

        String accessToken = jwtService.generateToken(appUser);

        return LoginDto.builder()
                .accessToken(accessToken)
                .build();
    }
}
