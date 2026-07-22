package lv.superchef.app.service;

import lv.superchef.app.model.Profile;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IProfileService {
    Optional<Profile> getProfileByUserId(Long userId);
    List<Profile> getAllProfiles();
    Profile getProfileById(Long id);

    Set<Long> getFollowedProfileIds(Long profileId);
}
