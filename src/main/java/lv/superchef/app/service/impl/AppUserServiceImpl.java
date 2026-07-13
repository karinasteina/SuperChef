package lv.superchef.app.service.impl;

import lv.superchef.app.dto.RegisterRequest;
import lv.superchef.app.model.AppUser;
import lv.superchef.app.model.Role;
import lv.superchef.app.repository.IAppUserRepo;
import lv.superchef.app.service.IAppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AppUserServiceImpl implements IAppUserService {

    @Autowired
    private IAppUserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AppUser register(RegisterRequest registerRequest) {
        if(registerRequest == null){
            throw new IllegalArgumentException("Register request is null");
        }

        if(registerRequest.getUsername() == null || registerRequest.getEmail() == null || registerRequest.getPassword()
                == null || registerRequest.getConfirmPassword() == null){
            throw new IllegalArgumentException("Incorrect input parameters");
        }

        if(!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())){
            throw new IllegalArgumentException("Passwords do not match");
        }

        if(userRepo.existsByUsername(registerRequest.getUsername())){
            throw new IllegalArgumentException("Username is already taken");
        }

        if(userRepo.existsByEmail(registerRequest.getEmail())){
            throw new IllegalArgumentException("Account with this email already exists");
        }


        AppUser newUser = new AppUser();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setEmail(registerRequest.getEmail());
        newUser.setRole(Role.ROLE_USER);

        return userRepo.save(newUser);
    }

}
