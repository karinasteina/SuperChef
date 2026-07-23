package lv.superchef.app.security;

import lv.superchef.app.model.AppUser;
import lv.superchef.app.repository.IAppUserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(AppUserDetailsService.class);

    private final IAppUserRepo userRepo;

    public AppUserDetailsService(IAppUserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!userRepo.existsByUsername(username)){
            throw new UsernameNotFoundException("Incorrect credentials");
        }
        AppUser appUser = userRepo.findByUsername(username);

        logger.debug("Found user: " + appUser.getUsername());
        return new AppUserDetails(appUser);
    }
}
