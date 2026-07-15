package lv.superchef.app.service;

import lv.superchef.app.model.Profile;

import java.util.Optional;

public interface IProfileService {
    Optional<Profile> getProfileByUserId(Long userId);
}
