package lv.superchef.app.service;

import lv.superchef.app.model.Profile;

public interface IProfileService {
    Profile getProfileByUserId(Long userId);
}
