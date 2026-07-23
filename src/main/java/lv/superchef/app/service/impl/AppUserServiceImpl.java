package lv.superchef.app.service.impl;

import jakarta.transaction.Transactional;
import lv.superchef.app.dto.RegisterRequest;
import lv.superchef.app.enums.Role;
import lv.superchef.app.model.AppUser;
import lv.superchef.app.model.Profile;
import lv.superchef.app.repository.IAppUserRepo;
import lv.superchef.app.repository.IProfileRepo;
import lv.superchef.app.service.IAppUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AppUserServiceImpl implements IAppUserService {

    private final IAppUserRepo userRepo;
    private final IProfileRepo profileRepo;
    private final PasswordEncoder passwordEncoder;

    public AppUserServiceImpl(IAppUserRepo userRepo, IProfileRepo profileRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
        this.passwordEncoder = passwordEncoder;
    }

   
    @Override
    @Transactional
    public AppUser register(RegisterRequest registerRequest) {
        if (registerRequest == null) {
            throw new IllegalArgumentException("Register request is null");
        }

        if (registerRequest.getUsername() == null || registerRequest.getEmail() == null || registerRequest.getPassword() == null || registerRequest.getConfirmPassword() == null) {
            throw new IllegalArgumentException("Incorrect input parameters");
        }

        if (!registerRequest
                .getPassword()
                .equals(registerRequest.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (userRepo.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        if (userRepo.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Account with this email already exists");
        }


        AppUser newUser = new AppUser();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setEmail(registerRequest.getEmail());
        newUser.setRole(Role.ROLE_USER);
        AppUser savedUser = userRepo.save(newUser);

        Profile profile = new Profile();
        profile.setDisplayName(savedUser.getUsername());
        profile.setBio("New SuperChef member");
        profile.setProfileImageUrl("/images/chef.jpg");
        profile.setAppUser(savedUser);

        profileRepo.save(profile);

        return savedUser;

    }

}
