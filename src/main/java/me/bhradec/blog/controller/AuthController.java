package me.bhradec.blog.controller;

import jakarta.validation.Valid;
import me.bhradec.blog.dto.AppUserDto;
import me.bhradec.blog.dto.LoginCommandDto;
import me.bhradec.blog.dto.LoginDto;
import me.bhradec.blog.exception.NotFoundException;
import me.bhradec.blog.mapper.AppUserMapper;
import me.bhradec.blog.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final AppUserMapper appUserMapper;

    public AuthController(AuthService authService, AppUserMapper appUserMapper) {
        this.authService = authService;
        this.appUserMapper = appUserMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginDto> login(@RequestBody @Valid LoginCommandDto loginCommandDto) {
        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(authService.login(loginCommandDto));
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        } catch (BadCredentialsException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, exception.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<AppUserDto> me() {
        return authService.getCurrentUser()
                .map(appUser -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(appUserMapper.toDto(appUser)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Current user not found."));
    }
}
