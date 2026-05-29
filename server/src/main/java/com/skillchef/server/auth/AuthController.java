package com.skillchef.server.auth;

import com.skillchef.server.auth.dto.AuthDtos.AuthResponse;
import com.skillchef.server.auth.dto.AuthDtos.LoginRequest;
import com.skillchef.server.auth.dto.AuthDtos.LogoutRequest;
import com.skillchef.server.auth.dto.AuthDtos.MessageResponse;
import com.skillchef.server.auth.dto.AuthDtos.RefreshRequest;
import com.skillchef.server.auth.dto.AuthDtos.SignupRequest;
import com.skillchef.server.auth.dto.AuthDtos.UserSummary;
import com.skillchef.server.auth.jwt.AuthPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** HTTP entrypoints for the auth feature. All paths under {@code /api/auth} are public. */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse signup(@Valid @RequestBody SignupRequest request) {
        return authService.signup(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    public MessageResponse logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
        return new MessageResponse("Logged out");
    }

    /**
     * Returns the currently authenticated user. Protected route — demonstrates the
     * JWT middleware and lets the frontend hydrate the session on load.
     */
    @GetMapping("/me")
    public ResponseEntity<UserSummary> me(@AuthenticationPrincipal AuthPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(authService.currentUser(principal.userId()));
    }
}
