package lv.superchef.app.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lv.superchef.app.model.Follow;
import lv.superchef.app.model.Profile;
import lv.superchef.app.repository.IFollowRepo;
import lv.superchef.app.repository.IProfileRepo;
import lv.superchef.app.service.IProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProfileServiceImpl implements IProfileService {
    @Autowired
    private IProfileRepo profileRepo;

    @Autowired
    private IFollowRepo followRepo;

    public Optional<Profile> getProfileByUserId(Long userId) {
        return profileRepo.findByAppUser_Id(userId);
    }

    @Override
    public List<Profile> getAllProfiles() {
        return profileRepo.findAll();
    }

    @Override
    public Profile getProfileById(Long id) {
        if(id == null || id <= 0){
            throw new IllegalArgumentException("Incorrect params");
        }

        return profileRepo.findById(id).orElseThrow(() -> new
                EntityNotFoundException("Profile with id " + id + " not found"));
    }

    @Override
    public Set<Long> getFollowedProfileIds(Long profileId) {
        if (profileId == null) {
            return Set.of();
        }

        return followRepo.findByFollower_Id(profileId)
                .stream()
                .map(Follow::getFollowing)
                .map(Profile::getId)
                .collect(Collectors.toSet());
    }
}
