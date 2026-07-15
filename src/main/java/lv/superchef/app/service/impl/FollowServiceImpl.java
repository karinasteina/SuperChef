package lv.superchef.app.service.impl;

import lv.superchef.app.model.Follow;
import lv.superchef.app.model.Profile;
import lv.superchef.app.repository.IFollowRepo;
import lv.superchef.app.repository.IProfileRepo;
import lv.superchef.app.service.IFollowService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FollowServiceImpl implements IFollowService {

    @Autowired
    private IFollowRepo followRepo;

    @Autowired
    private IProfileRepo profileRepo;

    @Override
    public void follow(Long followerId, Long followingId) {
        if(followerId == null || followingId == null){
            throw new IllegalArgumentException("Parameters are null");
        }
        if(followerId.equals(followingId)){
            throw new IllegalArgumentException("Cannot follow yourself");
        }

        if(followRepo.existsByFollowerIdAndFollowingId(followerId, followingId)){
            throw new IllegalStateException("Already following this profile");
        }

        Optional<Profile> follower = profileRepo.findByAppUser_Id(followerId);

        if (follower.isEmpty()) {
            throw new IllegalArgumentException("Profile with id " + followerId + " does not exist");
        }

        Optional<Profile> followedProfile = profileRepo.findByAppUser_Id(followingId);

        if(followedProfile.isEmpty()){
            throw new IllegalArgumentException("Profile with id " + followingId + " does not exist");
        }

        Follow follow = new Follow(follower.get(), followedProfile.get());
        followRepo.save(follow);

    }

    @Override
    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        if(followerId == null || followingId == null){
            throw new IllegalArgumentException("Parameters are null");
        }

        if(followerId.equals(followingId)){
            throw new IllegalArgumentException("Cannot unfollow yourself");
        }

        if(!followRepo.existsByFollowerIdAndFollowingId(followerId, followingId)){
            throw new IllegalStateException("Cannot unfollow a profile that was not followed");
        }

        followRepo.deleteByFollowerIdAndFollowingId(followerId, followingId);

    }

}