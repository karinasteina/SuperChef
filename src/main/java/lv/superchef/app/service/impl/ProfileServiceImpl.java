package lv.superchef.app.service.impl;

import lv.superchef.app.model.Profile;
import lv.superchef.app.repository.IProfileRepo;
import org.springframework.beans.factory.annotation.Autowired;

public class ProfileServiceImpl {
    @Autowired
    private IProfileRepo profileRepo;

    public Profile getProfileByUserId(Long userId) {
        return profileRepo.getProfileByUserId(userId);
    }

}
