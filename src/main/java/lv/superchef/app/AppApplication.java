package lv.superchef.app;

import jakarta.transaction.Transactional;
import lv.superchef.app.enums.Role;
import lv.superchef.app.model.AppUser;
import lv.superchef.app.model.Profile;
import lv.superchef.app.repository.IAppUserRepo;
import lv.superchef.app.repository.IProfileRepo;
import lv.superchef.app.service.impl.RecipeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import static lv.superchef.app.config.TempData.RECIPE_DATA;

@SpringBootApplication
public class AppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);

    }

    @Bean
    @Transactional
    public CommandLineRunner testDB(IAppUserRepo userRepo, PasswordEncoder passwordEncoder, RecipeService recipeService, IProfileRepo profileRepo) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                AppUser admin = userRepo.findByUsername("testadmin");

                if (admin == null) {
                    admin = new AppUser("testadmin", passwordEncoder.encode("testadmin123"), "test@test.lv", Role.ROLE_ADMIN);

                    admin = userRepo.save(admin);
                }

                createProfileIfMissing(admin, profileRepo);

                AppUser user = userRepo.findByUsername("testuser");

                if (user == null) {
                    user = new AppUser("testuser", passwordEncoder.encode("testuser123"), "test@user.lv", Role.ROLE_USER);

                    user = userRepo.save(user);
                }

                createProfileIfMissing(user, profileRepo);


                for (var recipe : RECIPE_DATA) {
                    recipeService.createRecipe(recipe);
                }
            }

            private void createProfileIfMissing(AppUser appUser, IProfileRepo profileRepo) {
                if (profileRepo
                        .findByAppUser_Id(appUser.getId())
                        .isEmpty()) {
                    Profile profile = new Profile();
                    profile.setDisplayName(appUser.getUsername());
                    profile.setBio("New SuperChef member");
                    profile.setProfileImageUrl("/images/chef.jpg");
                    profile.setAppUser(appUser);

                    profileRepo.save(profile);
                }
            }
        };

    }
}
