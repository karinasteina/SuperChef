package lv.superchef.app.controller;

import lv.superchef.app.model.Profile;
import lv.superchef.app.security.AppUserDetails;
import lv.superchef.app.service.IFollowService;
import lv.superchef.app.service.IProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.util.Optional;

@Controller
@RequestMapping("/profiles")
public class FollowController {
    @Autowired
    private IFollowService followService;

    @Autowired
    private IProfileService profileService;

    // userDetails -- user that follows and the id for the profile he wants to follow
    @PostMapping("/follow/{followingProfileId}")
    public ResponseEntity<Void> follow(@AuthenticationPrincipal AppUserDetails userDetails,
                                       @PathVariable Long followingProfileId,
                                       @RequestParam(required = false) String returnTo) {
        if(userDetails == null){
            return ResponseEntity.status(401).build();
        }

        Optional<Profile> follower = profileService.getProfileByUserId(userDetails.getUserId());

        if(follower.isEmpty()){
            return ResponseEntity.status(401).build();
        }

        followService.follow(follower.get().getId(), followingProfileId);
        return responseAfterChange(returnTo, followingProfileId);
    }

    @PostMapping("/unfollow/{unfollowProfileId}")
    public ResponseEntity<Void> unfollow(@AuthenticationPrincipal AppUserDetails userDetails,
                                         @PathVariable Long unfollowProfileId,
                                         @RequestParam(required = false) String returnTo) {
        if(userDetails == null){
            return ResponseEntity.status(401).build();
        }

        Optional<Profile> unfollower = profileService.getProfileByUserId(userDetails.getUserId());

        if(unfollower.isEmpty()){
            return ResponseEntity.status(401).build();
        }

        followService.unfollow(unfollower.get().getId(), unfollowProfileId);
        return responseAfterChange(returnTo, unfollowProfileId);
    }

    private ResponseEntity<Void> responseAfterChange(String returnTo, Long profileId) {
        if (returnTo == null) {
            return ResponseEntity.noContent().build();
        }

        String location = switch (returnTo) {
            case "profiles" -> "/profiles";
            case "profile" -> "/profile";
            case null, default -> "/profile/" + profileId;
        };

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .location(URI.create(location))
                .build();
    }
}
