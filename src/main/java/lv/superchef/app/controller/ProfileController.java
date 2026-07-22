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
    public String getControllerGetProfileById(@PathVariable Long id, Model model) {
        ProfileDTO profile = ProfileDTO.mapToDto(profileService.getProfileById(id));
        model.addAttribute("profile", profile);
        return "profile-detail-view"; // or whatever the single-profile template is called
    }

    @GetMapping("/profiles")
    public String getControllerGetAllProfiles(Model model) {
        List<ProfileDTO> profiles = profileService.getAllProfiles()
                .stream().map(ProfileDTO::mapToDto)
                .toList();

        model.addAttribute("profiles", profiles);
        return "all-profiles";
    }
}
