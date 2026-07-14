package lv.superchef.app.repository;

import lv.superchef.app.model.Profile;

public interface IProfileRepo {
    Profile getProfileByUserId(Long userId);
}
