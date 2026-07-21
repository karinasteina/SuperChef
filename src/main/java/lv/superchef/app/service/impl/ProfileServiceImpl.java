package lv.superchef.app.service.impl;

import lv.superchef.app.model.Profile;
import lv.superchef.app.repository.IProfileRepo;
import lv.superchef.app.service.IProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileServiceImpl implements IProfileService {
    @Autowired
    private IProfileRepo profileRepo;

    public Optional<Profile> getProfileByUserId(Long userId) {
        return profileRepo.findByAppUser_Id(userId);
    }

}
