package lv.superchef.app.controller;

import lv.superchef.app.dto.CreateRecipeDto;
import lv.superchef.app.dto.IngredientDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/recipes")
public class RecipeController {

    private final List<CreateRecipeDto> recipeData = List.of(
            CreateRecipeDto.builder()
                    .title("Creamy Garlic Pasta")
                    .description("Silky pasta coated in a rich garlic and Parmesan cream sauce.")
                    .calories(620)
                    .preparationTime(10)
                    .cookingTime(20)
                    .difficulty("Easy")
                    .category("Dinner")
                    .ingredients(List.of(
                            new IngredientDto("fettuccine", "200 g"),
                            new IngredientDto("garlic cloves", "3"),
                            new IngredientDto("heavy cream", "150 ml"),
                            new IngredientDto("Parmesan", "50 g")
                    ))
                    .steps(List.of(
                            "Cook the pasta until al dente.",
                            "Sauté the garlic and stir in the cream.",
                            "Add pasta and Parmesan, then toss well."
                    ))
                    .build(),

            CreateRecipeDto.builder()
                    .title("Fluffy Blueberry Pancakes")
                    .description("Light breakfast pancakes bursting with fresh blueberries.")
                    .calories(410)
                    .preparationTime(10)
                    .cookingTime(15)
                    .difficulty("Easy")
                    .category("Breakfast")
                    .ingredients(List.of(
                            new IngredientDto("flour", "180 g"),
                            new IngredientDto("milk", "250 ml"),
                            new IngredientDto("egg", "1"),
                            new IngredientDto("blueberries", "120 g")
                    ))
                    .steps(List.of(
                            "Whisk flour, milk, and egg into a batter.",
                            "Fold in the blueberries.",
                            "Cook spoonfuls on a hot pan until golden."
                    ))
                    .build(),

            CreateRecipeDto.builder()
                    .title("Roasted Tomato Soup")
                    .description("A comforting soup made with caramelized tomatoes and fresh basil.")
                    .calories(230)
                    .preparationTime(15)
                    .cookingTime(45)
                    .difficulty("Easy")
                    .category("Lunch")
                    .ingredients(List.of(
                            new IngredientDto("tomatoes", "800 g"),
                            new IngredientDto("onion", "1"),
                            new IngredientDto("vegetable stock", "500 ml"),
                            new IngredientDto("basil", "1 handful")
                    ))
                    .steps(List.of(
                            "Roast the tomatoes and onion until caramelized.",
                            "Simmer with vegetable stock.",
                            "Blend with basil until smooth."
                    ))
                    .build(),

            CreateRecipeDto.builder()
                    .title("Honey Soy Salmon")
                    .description("Tender glazed salmon with a sweet, savory, and slightly tangy finish.")
                    .calories(540)
                    .preparationTime(15)
                    .cookingTime(20)
                    .difficulty("Medium")
                    .category("Dinner")
                    .ingredients(List.of(
                            new IngredientDto("salmon fillets", "2"),
                            new IngredientDto("soy sauce", "3 tbsp"),
                            new IngredientDto("honey", "2 tbsp"),
                            new IngredientDto("grated ginger", "1 tsp")
                    ))
                    .steps(List.of(
                            "Mix soy sauce, honey, and ginger.",
                            "Marinate the salmon for ten minutes.",
                            "Bake and brush with glaze until cooked."
                    ))
                    .build(),

            CreateRecipeDto.builder()
                    .title("Chocolate Lava Cake")
                    .description("Individual chocolate cakes with warm, flowing centers.")
                    .calories(470)
                    .preparationTime(20)
                    .cookingTime(12)
                    .difficulty("Hard")
                    .category("Dessert")
                    .ingredients(List.of(
                            new IngredientDto("dark chocolate", "100 g"),
                            new IngredientDto("butter", "100 g"),
                            new IngredientDto("eggs", "2"),
                            new IngredientDto("sugar", "50 g")
                    ))
                    .steps(List.of(
                            "Melt the chocolate and butter together.",
                            "Whisk in eggs and sugar.",
                            "Bake in ramekins until the edges set."
                    ))
                    .build(),

            CreateRecipeDto.builder()
                    .title("Chicken Caesar Wrap")
                    .description("A crisp, portable lunch filled with chicken and creamy Caesar salad.")
                    .calories(490)
                    .preparationTime(15)
                    .cookingTime(10)
                    .difficulty("Easy")
                    .category("Lunch")
                    .ingredients(List.of(
                            new IngredientDto("flour tortillas", "2"),
                            new IngredientDto("chicken breast", "200 g"),
                            new IngredientDto("romaine heart", "1"),
                            new IngredientDto("Caesar dressing", "3 tbsp")
                    ))
                    .steps(List.of(
                            "Season and cook the chicken.",
                            "Toss chopped romaine with dressing.",
                            "Fill the tortillas and roll tightly."
                    ))
                    .build(),

            CreateRecipeDto.builder()
                    .title("Vegetable Fried Rice")
                    .description("Quick wok-fried rice packed with colorful vegetables and sesame flavor.")
                    .calories(380)
                    .preparationTime(15)
                    .cookingTime(12)
                    .difficulty("Medium")
                    .category("Dinner")
                    .ingredients(List.of(
                            new IngredientDto("cooked rice", "300 g"),
                            new IngredientDto("mixed vegetables", "150 g"),
                            new IngredientDto("eggs", "2"),
                            new IngredientDto("soy sauce", "2 tbsp")
                    ))
                    .steps(List.of(
                            "Stir-fry the vegetables over high heat.",
                            "Scramble the eggs in the wok.",
                            "Add rice and soy sauce, then toss."
                    ))
                    .build(),

            CreateRecipeDto.builder()
                    .title("Greek Yogurt Parfait")
                    .description("Creamy yogurt layered with berries, crunchy granola, and honey.")
                    .calories(320)
                    .preparationTime(8)
                    .cookingTime(0)
                    .difficulty("Easy")
                    .category("Breakfast")
                    .ingredients(List.of(
                            new IngredientDto("Greek yogurt", "250 g"),
                            new IngredientDto("mixed berries", "100 g"),
                            new IngredientDto("granola", "60 g"),
                            new IngredientDto("honey", "1 tbsp")
                    ))
                    .steps(List.of(
                            "Spoon yogurt into two glasses.",
                            "Add layers of berries and granola.",
                            "Finish with a drizzle of honey."
                    ))
                    .build(),

            CreateRecipeDto.builder()
                    .title("Cinnamon Apple Crumble")
                    .description("Soft spiced apples beneath a crisp, buttery oat topping.")
                    .calories(390)
                    .preparationTime(20)
                    .cookingTime(35)
                    .difficulty("Medium")
                    .category("Dessert")
                    .ingredients(List.of(
                            new IngredientDto("apples", "4"),
                            new IngredientDto("cinnamon", "1 tsp"),
                            new IngredientDto("rolled oats", "100 g"),
                            new IngredientDto("butter", "75 g")
                    ))
                    .steps(List.of(
                            "Toss sliced apples with cinnamon.",
                            "Rub oats and butter into crumbs.",
                            "Top the apples and bake until golden."
                    ))
                    .build(),

            CreateRecipeDto.builder()
                    .title("Mango Banana Smoothie")
                    .description("A naturally sweet and refreshing tropical drink for any time of day.")
                    .calories(260)
                    .preparationTime(5)
                    .cookingTime(0)
                    .difficulty("Easy")
                    .category("Drinks")
                    .ingredients(List.of(
                            new IngredientDto("ripe mango", "1"),
                            new IngredientDto("banana", "1"),
                            new IngredientDto("milk", "250 ml"),
                            new IngredientDto("ice cubes", "4")
                    ))
                    .steps(List.of(
                            "Peel and chop the fruit.",
                            "Add all ingredients to a blender.",
                            "Blend until smooth and serve cold."
                    ))
                    .build()
    );

    @GetMapping
    public String listOfRecipes(Model model) {
        model.addAttribute("recipes", recipeData);
        return "recipe/list";
    }

    @GetMapping("/create")
    public String showCreateRecipePage(Model model) {
        model.addAttribute("createRecipeDto", new CreateRecipeDto());
        return "recipe/create";
    }

    @PostMapping
    public String handleCreateRecipe(
            @ModelAttribute CreateRecipeDto createRecipeDto,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage
    ) {
        // TODO: Pass to service layer once backend is wired
        System.out.println("Received recipe: " + createRecipeDto.getTitle());
        return "redirect:/recipes/create?success=true";
    }

}
