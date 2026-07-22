package lv.superchef.app.controller;

import lv.superchef.app.dto.ProfileDTO;
import lv.superchef.app.model.Profile;
import lv.superchef.app.security.AppUserDetails;
import lv.superchef.app.service.IProfileService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Set;

@Controller
public class ProfileController {
    private final IProfileService profileService;

    public ProfileController(IProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal AppUserDetails userDetails, Model model) {
        Profile profile = profileService.getProfileByUserId(userDetails.getUserId())
                .orElseThrow(() -> new IllegalStateException("Profile not found"));
        model.addAttribute("activePage", "profile");
        model.addAttribute("user", userDetails);
        model.addAttribute("profile", profile);

        return "profile";
    }

    @GetMapping("/profile/{id}")
    public String getControllerGetProfileById(@PathVariable Long id,
                                              @AuthenticationPrincipal AppUserDetails userDetails,
                                              Model model) {
        ProfileDTO profile = ProfileDTO.mapToDto(profileService.getProfileById(id));
        Profile currentProfile = userDetails == null ? null : getCurrentProfile(userDetails);
        Set<Long> followedProfileIds = currentProfile == null
                ? Set.of()
                : profileService.getFollowedProfileIds(currentProfile.getId());

        model.addAttribute("activePage", "profiles");
        model.addAttribute("profile", profile);
        model.addAttribute("currentProfileId", currentProfile == null ? null : currentProfile.getId());
        model.addAttribute("followedProfileIds", followedProfileIds);
        return "profile-detail-view";
    }

    @GetMapping("/profiles")
    public String getControllerGetAllProfiles(@AuthenticationPrincipal AppUserDetails userDetails,
                                              Model model) {
        List<ProfileDTO> profiles = profileService.getAllProfiles()
                .stream().map(ProfileDTO::mapToDto)
                .toList();
        Profile currentProfile = userDetails == null ? null : getCurrentProfile(userDetails);

        model.addAttribute("activePage", "profiles");
        model.addAttribute("profiles", profiles);
        model.addAttribute("currentProfileId", currentProfile == null ? null : currentProfile.getId());
        model.addAttribute("followedProfileIds", currentProfile == null
                ? Set.of()
                : profileService.getFollowedProfileIds(currentProfile.getId()));
        return "all-profiles";
    }

    private Profile getCurrentProfile(AppUserDetails userDetails) {
        return profileService.getProfileByUserId(userDetails.getUserId())
                .orElseThrow(() -> new IllegalStateException("Profile not found"));
    }
}
