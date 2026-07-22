package lv.superchef.app.controller;

import lv.superchef.app.dto.ProfileDTO;
import lv.superchef.app.model.Profile;
import lv.superchef.app.service.IProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@RequestMapping("/profiles")
public class ProfileController {
    @Autowired
    private IProfileService profileService;
    // tests later
    @GetMapping
    public String getControllerGetAllProfiles(Model model){
        List<ProfileDTO> profiles = profileService.getAllProfiles()
                .stream().map(ProfileDTO::mapToDto)
                .toList();

        model.addAttribute("profiles", profiles);
        return "profile-view";  // or another view where u need this

    }
    // tests later
    @GetMapping("/{id}")
    public String getControllerGetProfileById(@PathVariable Long id, Model model){
        ProfileDTO profile = ProfileDTO.mapToDto(profileService.getProfileById(id));
        model.addAttribute("profile", profile);
        return "profile-detail-view"; // or whatever the single-profile template is called
    }

}
