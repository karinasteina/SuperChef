package lv.superchef.app.service;

import lv.superchef.app.model.Profile;
import lv.superchef.app.model.Recipe;

import java.util.List;
import java.util.Optional;

public interface IProfileService {
    Optional<Profile> getProfileByUserId(Long userId);
    List<Profile> getAllProfiles();
    Profile getProfileById(Long id);



}
