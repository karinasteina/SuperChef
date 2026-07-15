package lv.superchef.app;

import lv.superchef.app.enums.Role;
import lv.superchef.app.model.AppUser;
import lv.superchef.app.repository.IAppUserRepo;
import lv.superchef.app.service.IRecipeService;
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
    public CommandLineRunner testDB(IAppUserRepo userRepo, PasswordEncoder passwordEncoder, IRecipeService recipeService) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                if (!userRepo.existsByUsername("testadmin")) {
                    AppUser appUserAdmin = new AppUser("testadmin", passwordEncoder.encode("testadmin123"), "test@test.lv", Role.ROLE_ADMIN);
                    userRepo.save(appUserAdmin);
                }

                if (!userRepo.existsByUsername("testuser")) {
                    AppUser appUser = new AppUser("testuser", passwordEncoder.encode("testuser123"), "test@user.lv", Role.ROLE_USER);
                    userRepo.save(appUser);
                }

                for (var recipe : RECIPE_DATA) {
                    recipeService.createRecipe(recipe);
                }
            }


        };

    }
}
