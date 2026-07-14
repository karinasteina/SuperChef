package lv.superchef.app;

import lv.superchef.app.model.AppUser;
import lv.superchef.app.enums.Role;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.repository.IAppUserRepo;
import lv.superchef.app.repository.IRecipeRepo;
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
	public CommandLineRunner testDB(IAppUserRepo userRepo, PasswordEncoder passwordEncoder, IRecipeRepo recipeRepository) {
		return new CommandLineRunner() {
			@Override
			public void run(String... args) throws Exception {
				AppUser appUserAdmin = new AppUser("testadmin", passwordEncoder.encode("testadmin123"), "test@test.lv", Role.ROLE_ADMIN);
				userRepo.save(appUserAdmin);

				AppUser appUser = new AppUser("testuser", passwordEncoder.encode("testuser123"), "test@user.lv", Role.ROLE_USER);
				userRepo.save(appUser);

				recipeRepository.save(buildRecipe("Creamy Garlic Pasta",
						"Silky pasta coated in a rich garlic and Parmesan cream sauce.",
						620, 10, 20, "Easy", "Dinner"));

				recipeRepository.save(buildRecipe("Fluffy Blueberry Pancakes",
						"Light breakfast pancakes bursting with fresh blueberries.",
						410, 10, 15, "Easy", "Breakfast"));

				recipeRepository.save(buildRecipe("Roasted Tomato Soup",
						"A comforting soup made with caramelized tomatoes and fresh basil.",
						230, 15, 45, "Easy", "Lunch"));

				recipeRepository.save(buildRecipe("Honey Soy Salmon",
						"Tender glazed salmon with a sweet, savory, and slightly tangy finish.",
						540, 15, 20, "Medium", "Dinner"));

				recipeRepository.save(buildRecipe("Chocolate Lava Cake",
						"Individual chocolate cakes with warm, flowing centers.",
						470, 20, 12, "Hard", "Dessert"));

				recipeRepository.save(buildRecipe("Chicken Caesar Wrap",
						"A crisp, portable lunch filled with chicken and creamy Caesar salad.",
						490, 15, 10, "Easy", "Lunch"));

				recipeRepository.save(buildRecipe("Vegetable Fried Rice",
						"Quick wok-fried rice packed with colorful vegetables and sesame flavor.",
						380, 15, 12, "Medium", "Dinner"));

				recipeRepository.save(buildRecipe("Greek Yogurt Parfait",
						"Creamy yogurt layered with berries, crunchy granola, and honey.",
						320, 8, 0, "Easy", "Breakfast"));

				recipeRepository.save(buildRecipe("Cinnamon Apple Crumble",
						"Soft spiced apples beneath a crisp, buttery oat topping.",
						390, 20, 35, "Medium", "Dessert"));

				recipeRepository.save(buildRecipe("Mango Banana Smoothie",
						"A naturally sweet and refreshing tropical drink for any time of day.",
						260, 5, 0, "Easy", "Drinks"));
			}

			private Recipe buildRecipe(String title, String description, int calories,
			                           int prepTime, int cookTime, String difficulty, String category) {
				Recipe recipe = new Recipe();
				recipe.setTitle(title);
				recipe.setDescription(description);
				recipe.setCalories(calories);
				recipe.setPreparationTime(prepTime);
				recipe.setCookingTime(cookTime);
				recipe.setDifficulty(difficulty);
				recipe.setCategory(category);
				return recipe;


			}


		};

	}
}
