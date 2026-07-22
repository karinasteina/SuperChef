package lv.superchef.app.service;

import lv.superchef.app.model.Profile;
import lv.superchef.app.model.Recipe;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IProfileService {
    Optional<Profile> getProfileByUserId(Long userId);
    List<Profile> getAllProfiles();
    Profile getProfileById(Long id);

    List<Recipe> getRecipesByProfileId(Long profileId);

    List<Profile> getFollowedProfiles(Long profileId);

    Set<Long> getFollowedProfileIds(Long profileId);
}
