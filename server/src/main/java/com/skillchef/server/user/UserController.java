package com.skillchef.server.user;

import com.skillchef.server.auth.jwt.AuthPrincipal;
import com.skillchef.server.user.UserDtos.AvatarResponse;
import com.skillchef.server.user.UserDtos.PreferencesDto;
import com.skillchef.server.user.UserDtos.ProfileResponse;
import com.skillchef.server.user.UserDtos.PublicProfileResponse;
import com.skillchef.server.user.UserDtos.UpdateProfileRequest;
import com.skillchef.server.user.UserDtos.XpLevelResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * HTTP entrypoints for the User Service: profile CRUD, avatar upload,
 * preferences, public profile view, and XP/level reads.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ProfileResponse getOwnProfile(@AuthenticationPrincipal AuthPrincipal principal) {
        return userService.getOwnProfile(requireUser(principal));
    }

    @PutMapping("/me")
    public ProfileResponse updateOwnProfile(@AuthenticationPrincipal AuthPrincipal principal,
                                            @Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateOwnProfile(requireUser(principal), request);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOwnProfile(@AuthenticationPrincipal AuthPrincipal principal) {
        userService.deleteOwnProfile(requireUser(principal));
    }

    @GetMapping("/me/preferences")
    public PreferencesDto getPreferences(@AuthenticationPrincipal AuthPrincipal principal) {
        return userService.getPreferences(requireUser(principal));
    }

    @PutMapping("/me/preferences")
    public PreferencesDto updatePreferences(@AuthenticationPrincipal AuthPrincipal principal,
                                            @Valid @RequestBody PreferencesDto preferences) {
        return userService.updatePreferences(requireUser(principal), preferences);
    }

    @PostMapping("/me/avatar")
    public ResponseEntity<AvatarResponse> uploadAvatar(@AuthenticationPrincipal AuthPrincipal principal,
                                                       @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.uploadAvatar(requireUser(principal), file));
    }

    @GetMapping("/{userId}")
    public PublicProfileResponse getPublicProfile(@PathVariable UUID userId) {
        return userService.getPublicProfile(userId);
    }

    @GetMapping("/{userId}/xp")
    public XpLevelResponse getXpLevel(@PathVariable UUID userId) {
        return userService.getXpLevel(userId);
    }

    private UUID requireUser(AuthPrincipal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return principal.userId();
    }
}
