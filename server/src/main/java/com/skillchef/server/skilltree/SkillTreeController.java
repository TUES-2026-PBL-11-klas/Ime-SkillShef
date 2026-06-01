package com.skillchef.server.skilltree;

import com.skillchef.server.auth.jwt.AuthPrincipal;
import com.skillchef.server.skilltree.SkillTreeDtos.ProgressDto;
import com.skillchef.server.skilltree.SkillTreeDtos.SkillTreeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/skills")
public class SkillTreeController {

    private final SkillTreeService skillTreeService;

    public SkillTreeController(SkillTreeService skillTreeService) {
        this.skillTreeService = skillTreeService;
    }

    @GetMapping("/tree")
    public SkillTreeResponse getTree(@AuthenticationPrincipal AuthPrincipal principal) {
        UUID userId = requireUser(principal);
        return skillTreeService.getTree(userId);
    }

    @PostMapping("/nodes/{nodeId}/unlock")
    public ResponseEntity<ProgressDto> unlockNode(@AuthenticationPrincipal AuthPrincipal principal,
                                                  @PathVariable UUID nodeId) {
        UUID userId = requireUser(principal);
        return ResponseEntity.ok(skillTreeService.unlockNode(userId, nodeId));
    }

    @PostMapping("/nodes/{nodeId}/complete")
    public ResponseEntity<ProgressDto> completeNode(@AuthenticationPrincipal AuthPrincipal principal,
                                                    @PathVariable UUID nodeId) {
        UUID userId = requireUser(principal);
        return ResponseEntity.ok(skillTreeService.completeNode(userId, nodeId));
    }

    private UUID requireUser(AuthPrincipal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return principal.userId();
    }
}
