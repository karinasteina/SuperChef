package lv.superchef.app.controller;

import lv.superchef.app.model.Profile;
import lv.superchef.app.security.AppUserDetails;
import lv.superchef.app.service.IFollowService;
import lv.superchef.app.service.IProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
public class FollowController {
    @Autowired
    private IFollowService followService;

    @Autowired
    private IProfileService profileService;

    // userDetails - user that follows and the id for the profile he wants to follow
    @PostMapping
    public void follow(@AuthenticationPrincipal AppUserDetails userDetails, @PathVariable Long followingProfileId){
        if(userDetails == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in");
        }

        Optional<Profile> follower = profileService.getProfileByUserId(userDetails.getUserId());

        if(follower.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        followService.follow(follower.get().getId(), followingProfileId);

    }

}
