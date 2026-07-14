package lv.superchef.app;

import lv.superchef.app.model.AppUser;
import lv.superchef.app.model.Role;
import lv.superchef.app.repository.IAppUserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class AppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);

    }

    @Bean
    public CommandLineRunner testDB(IAppUserRepo userRepo, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepo.existsByUsername("testadmin")) {
                AppUser admin = new AppUser("testadmin", passwordEncoder.encode("testadmin123"), "test@test.lv", Role.ROLE_ADMIN);

                userRepo.save(admin);
            }

            if (!userRepo.existsByUsername("testuser")) {
                AppUser user = new AppUser("testuser", passwordEncoder.encode("testuser123"), "test@user.lv", Role.ROLE_USER);

                userRepo.save(user);
            }
            if (!userRepo.existsByUsername("test2")) {
                AppUser user = new AppUser("test2", passwordEncoder.encode("testuser123"), "test2@user.lv", Role.ROLE_USER);

                userRepo.save(user);
            }
        };
    }

}
