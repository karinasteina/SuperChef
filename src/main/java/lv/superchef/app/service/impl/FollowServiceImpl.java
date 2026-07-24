package lv.superchef.app.service.impl;

import lv.superchef.app.model.Follow;
import lv.superchef.app.model.Profile;
import lv.superchef.app.repository.IFollowRepo;
import lv.superchef.app.repository.IProfileRepo;
import lv.superchef.app.service.IFollowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FollowServiceImpl implements IFollowService {

    private final IFollowRepo followRepo;
    private final IProfileRepo profileRepo;

    public FollowServiceImpl(IFollowRepo followRepo, IProfileRepo profileRepo) {
        this.followRepo = followRepo;
        this.profileRepo = profileRepo;
    }

    @Override
    public void follow(Long followerId, Long followingId) {
        if (followerId == null || followingId == null) {
            throw new IllegalArgumentException("Parameters are null");
        }
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("Cannot follow yourself");
        }

        if (followRepo.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            return;
        }

        Optional<Profile> follower = profileRepo.findById(followerId);

        if (follower.isEmpty()) {
            throw new IllegalArgumentException("Profile with id " + followerId + " does not exist");
        }

        Optional<Profile> followedProfile = profileRepo.findById(followingId);

        if (followedProfile.isEmpty()) {
            throw new IllegalArgumentException("Profile with id " + followingId + " does not exist");
        }

        Follow follow = new Follow(follower.get(), followedProfile.get());
        followRepo.save(follow);

    }

    @Override
    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        if (followerId == null || followingId == null) {
            throw new IllegalArgumentException("Parameters are null");
        }

        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("Cannot unfollow yourself");
        }

        if (!followRepo.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            return;
        }

        followRepo.deleteByFollowerIdAndFollowingId(followerId, followingId);

    }

}
