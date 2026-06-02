package com.skillchef.server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillchef.server.storage.StorageService;
import com.skillchef.server.user.UserDtos.AvatarResponse;
import com.skillchef.server.user.UserDtos.PreferencesDto;
import com.skillchef.server.user.UserDtos.ProfileResponse;
import com.skillchef.server.user.UserDtos.PublicProfileResponse;
import com.skillchef.server.user.UserDtos.UpdateProfileRequest;
import com.skillchef.server.user.UserDtos.XpLevelResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * Application logic for the User Service: profile CRUD, avatar upload,
 * preferences, public profile view, and XP/level reads.
 */
@Service
public class UserService {

    private static final String AVATAR_FOLDER = "avatars";

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final StorageService storageService;
    private final ObjectMapper objectMapper;

    public UserService(UserRepository userRepository,
                       ProfileRepository profileRepository,
                       StorageService storageService,
                       ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.storageService = storageService;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public ProfileResponse getOwnProfile(UUID userId) {
        User user = requireUser(userId);
        Profile profile = profileRepository.findByUser_Id(userId).orElse(null);
        return toProfileResponse(user, profile);
    }

    @Transactional(readOnly = true)
    public PublicProfileResponse getPublicProfile(UUID userId) {
        User user = requireUser(userId);
        Profile profile = profileRepository.findByUser_Id(userId).orElse(null);
        return new PublicProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getAvatarUrl(),
                profile == null ? null : profile.getBio(),
                user.getGlobalXp(),
                user.getLevel(),
                user.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public XpLevelResponse getXpLevel(UUID userId) {
        User user = requireUser(userId);
        return new XpLevelResponse(user.getId(), user.getGlobalXp(), user.getLevel());
    }

    @Transactional
    public ProfileResponse updateOwnProfile(UUID userId, UpdateProfileRequest request) {
        User user = requireUser(userId);
        Profile profile = getOrCreateProfile(user);

        if (request.username() != null && !request.username().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.username())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "This username is taken");
            }
            user.setUsername(request.username());
            userRepository.save(user);
        }

        if (request.bio() != null) {
            profile.setBio(request.bio());
        }
        if (request.preferences() != null) {
            profile.setPreferences(writePreferences(request.preferences()));
        }
        profileRepository.save(profile);

        return toProfileResponse(user, profile);
    }

    @Transactional
    public PreferencesDto updatePreferences(UUID userId, PreferencesDto preferences) {
        User user = requireUser(userId);
        Profile profile = getOrCreateProfile(user);
        profile.setPreferences(writePreferences(preferences));
        profileRepository.save(profile);
        return preferences;
    }

    @Transactional(readOnly = true)
    public PreferencesDto getPreferences(UUID userId) {
        requireUser(userId);
        Profile profile = profileRepository.findByUser_Id(userId).orElse(null);
        return readPreferences(profile);
    }

    @Transactional
    public AvatarResponse uploadAvatar(UUID userId, MultipartFile file) {
        User user = requireUser(userId);
        String url = storageService.store(AVATAR_FOLDER, file);
        user.setAvatarUrl(url);
        userRepository.save(user);
        return new AvatarResponse(url);
    }

    @Transactional
    public void deleteOwnProfile(UUID userId) {
        User user = requireUser(userId);
        userRepository.delete(user);
    }

    private Profile getOrCreateProfile(User user) {
        return profileRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    Profile fresh = new Profile();
                    fresh.setUser(user);
                    return fresh;
                });
    }

    private ProfileResponse toProfileResponse(User user, Profile profile) {
        return new ProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl(),
                profile == null ? null : profile.getBio(),
                readPreferences(profile),
                user.getGlobalXp(),
                user.getLevel(),
                user.getCreatedAt());
    }

    private PreferencesDto readPreferences(Profile profile) {
        if (profile == null || profile.getPreferences() == null || profile.getPreferences().isBlank()) {
            return PreferencesDto.defaults();
        }
        try {
            return objectMapper.readValue(profile.getPreferences(), PreferencesDto.class);
        } catch (Exception ex) {
            return PreferencesDto.defaults();
        }
    }

    private String writePreferences(PreferencesDto preferences) {
        try {
            return objectMapper.writeValueAsString(preferences);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to serialize preferences");
        }
    }

    private User requireUser(UUID userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
