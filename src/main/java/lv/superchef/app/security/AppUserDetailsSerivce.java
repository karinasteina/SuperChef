package lv.superchef.app.security;

import lv.superchef.app.model.AppUser;
import lv.superchef.app.repository.IAppUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsSerivce implements UserDetailsService {
    @Autowired
    private IAppUserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!userRepo.existsByUsername(username)){
            throw new UsernameNotFoundException("Incorrect credentials");
        }
        AppUser appUser = userRepo.findByUsername(username);
        System.out.println(">>> Found user: " + appUser.getUsername() + " | stored hash: " + appUser.getPassword());
        return new AppUserDetails(appUser);
    }
}
