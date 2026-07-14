package lv.superchef.app.config;

import lv.superchef.app.dto.IngredientInputDTO;
import lv.superchef.app.dto.RecipeCreateDTO;
import lv.superchef.app.enums.IngredientUnit;

import java.util.List;

public final class TempData {

    public static final List<RecipeCreateDTO> RECIPE_DATA = List.of(
            RecipeCreateDTO.builder()
                    .title("Creamy Garlic Pasta")
                    .description("Silky pasta coated in a rich garlic and Parmesan cream sauce.")
                    .calories(620)
                    .preparationTime(10)
                    .cookingTime(20)
                    .difficulty("Easy")
                    .category("Dinner")
                    .ingredients(List.of(
                            new IngredientInputDTO("fettuccine", 200.0, IngredientUnit.G),
                            new IngredientInputDTO("garlic cloves", 3.0, IngredientUnit.CLOVE),
                            new IngredientInputDTO("heavy cream", 150.0, IngredientUnit.ML),
                            new IngredientInputDTO("Parmesan", 50.0, IngredientUnit.G)
                    ))
                    .steps(List.of(
                            "Cook the pasta until al dente.",
                            "Sauté the garlic and stir in the cream.",
                            "Add pasta and Parmesan, then toss well."
                    ))
                    .build(),

            RecipeCreateDTO.builder()
                    .title("Fluffy Blueberry Pancakes")
                    .description("Light breakfast pancakes bursting with fresh blueberries.")
                    .calories(410)
                    .preparationTime(10)
                    .cookingTime(15)
                    .difficulty("Easy")
                    .category("Breakfast")
                    .ingredients(List.of(
                            new IngredientInputDTO("flour", 180.0, IngredientUnit.G),
                            new IngredientInputDTO("milk", 250.0, IngredientUnit.ML),
                            new IngredientInputDTO("egg", 1.0, IngredientUnit.PIECE),
                            new IngredientInputDTO("blueberries", 120.0, IngredientUnit.G)
                    ))
                    .steps(List.of(
                            "Whisk flour, milk, and egg into a batter.",
                            "Fold in the blueberries.",
                            "Cook spoonfuls on a hot pan until golden."
                    ))
                    .build(),

            RecipeCreateDTO.builder()
                    .title("Roasted Tomato Soup")
                    .description("A comforting soup made with caramelized tomatoes and fresh basil.")
                    .calories(230)
                    .preparationTime(15)
                    .cookingTime(45)
                    .difficulty("Easy")
                    .category("Lunch")
                    .ingredients(List.of(
                            new IngredientInputDTO("tomatoes", 800.0, IngredientUnit.G),
                            new IngredientInputDTO("onion", 1.0, IngredientUnit.PIECE),
                            new IngredientInputDTO("vegetable stock", 500.0, IngredientUnit.ML),
                            new IngredientInputDTO("basil", 1.0, IngredientUnit.BUNCH)
                    ))
                    .steps(List.of(
                            "Roast the tomatoes and onion until caramelized.",
                            "Simmer with vegetable stock.",
                            "Blend with basil until smooth."
                    ))
                    .build(),

            RecipeCreateDTO.builder()
                    .title("Honey Soy Salmon")
                    .description("Tender glazed salmon with a sweet, savory, and slightly tangy finish.")
                    .calories(540)
                    .preparationTime(15)
                    .cookingTime(20)
                    .difficulty("Medium")
                    .category("Dinner")
                    .ingredients(List.of(
                            new IngredientInputDTO("salmon fillets", 2.0, IngredientUnit.PIECE),
                            new IngredientInputDTO("soy sauce", 3.0, IngredientUnit.TBSP),
                            new IngredientInputDTO("honey", 2.0, IngredientUnit.TBSP),
                            new IngredientInputDTO("grated ginger", 1.0, IngredientUnit.TSP)
                    ))
                    .steps(List.of(
                            "Mix soy sauce, honey, and ginger.",
                            "Marinate the salmon for ten minutes.",
                            "Bake and brush with glaze until cooked."
                    ))
                    .build(),

            RecipeCreateDTO.builder()
                    .title("Chocolate Lava Cake")
                    .description("Individual chocolate cakes with warm, flowing centers.")
                    .calories(470)
                    .preparationTime(20)
                    .cookingTime(12)
                    .difficulty("Hard")
                    .category("Dessert")
                    .ingredients(List.of(
                            new IngredientInputDTO("dark chocolate", 100.0, IngredientUnit.G),
                            new IngredientInputDTO("butter", 100.0, IngredientUnit.G),
                            new IngredientInputDTO("eggs", 2.0, IngredientUnit.PIECE),
                            new IngredientInputDTO("sugar", 50.0, IngredientUnit.G)
                    ))
                    .steps(List.of(
                            "Melt the chocolate and butter together.",
                            "Whisk in eggs and sugar.",
                            "Bake in ramekins until the edges set."
                    ))
                    .build(),

            RecipeCreateDTO.builder()
                    .title("Chicken Caesar Wrap")
                    .description("A crisp, portable lunch filled with chicken and creamy Caesar salad.")
                    .calories(490)
                    .preparationTime(15)
                    .cookingTime(10)
                    .difficulty("Easy")
                    .category("Lunch")
                    .ingredients(List.of(
                            new IngredientInputDTO("flour tortillas", 2.0, IngredientUnit.PIECE),
                            new IngredientInputDTO("chicken breast", 200.0, IngredientUnit.G),
                            new IngredientInputDTO("romaine heart", 1.0, IngredientUnit.PIECE),
                            new IngredientInputDTO("Caesar dressing", 3.0, IngredientUnit.TBSP)
                    ))
                    .steps(List.of(
                            "Season and cook the chicken.",
                            "Toss chopped romaine with dressing.",
                            "Fill the tortillas and roll tightly."
                    ))
                    .build(),

            RecipeCreateDTO.builder()
                    .title("Vegetable Fried Rice")
                    .description("Quick wok-fried rice packed with colorful vegetables and sesame flavor.")
                    .calories(380)
                    .preparationTime(15)
                    .cookingTime(12)
                    .difficulty("Medium")
                    .category("Dinner")
                    .ingredients(List.of(
                            new IngredientInputDTO("cooked rice", 300.0, IngredientUnit.G),
                            new IngredientInputDTO("mixed vegetables", 150.0, IngredientUnit.G),
                            new IngredientInputDTO("eggs", 2.0, IngredientUnit.PIECE),
                            new IngredientInputDTO("soy sauce", 2.0, IngredientUnit.TBSP)
                    ))
                    .steps(List.of(
                            "Stir-fry the vegetables over high heat.",
                            "Scramble the eggs in the wok.",
                            "Add rice and soy sauce, then toss."
                    ))
                    .build(),

            RecipeCreateDTO.builder()
                    .title("Greek Yogurt Parfait")
                    .description("Creamy yogurt layered with berries, crunchy granola, and honey.")
                    .calories(320)
                    .preparationTime(8)
                    .cookingTime(0)
                    .difficulty("Easy")
                    .category("Breakfast")
                    .ingredients(List.of(
                            new IngredientInputDTO("Greek yogurt", 250.0, IngredientUnit.G),
                            new IngredientInputDTO("mixed berries", 100.0, IngredientUnit.G),
                            new IngredientInputDTO("granola", 60.0, IngredientUnit.G),
                            new IngredientInputDTO("honey", 1.0, IngredientUnit.TBSP)
                    ))
                    .steps(List.of(
                            "Spoon yogurt into two glasses.",
                            "Add layers of berries and granola.",
                            "Finish with a drizzle of honey."
                    ))
                    .build(),

            RecipeCreateDTO.builder()
                    .title("Cinnamon Apple Crumble")
                    .description("Soft spiced apples beneath a crisp, buttery oat topping.")
                    .calories(390)
                    .preparationTime(20)
                    .cookingTime(35)
                    .difficulty("Medium")
                    .category("Dessert")
                    .ingredients(List.of(
                            new IngredientInputDTO("apples", 4.0, IngredientUnit.PIECE),
                            new IngredientInputDTO("cinnamon", 1.0, IngredientUnit.TSP),
                            new IngredientInputDTO("rolled oats", 100.0, IngredientUnit.G),
                            new IngredientInputDTO("butter", 75.0, IngredientUnit.G)
                    ))
                    .steps(List.of(
                            "Toss sliced apples with cinnamon.",
                            "Rub oats and butter into crumbs.",
                            "Top the apples and bake until golden."
                    ))
                    .build(),

            RecipeCreateDTO.builder()
                    .title("Mango Banana Smoothie")
                    .description("A naturally sweet and refreshing tropical drink for any time of day.")
                    .calories(260)
                    .preparationTime(5)
                    .cookingTime(0)
                    .difficulty("Easy")
                    .category("Drinks")
                    .ingredients(List.of(
                            new IngredientInputDTO("ripe mango", 1.0, IngredientUnit.PIECE),
                            new IngredientInputDTO("banana", 1.0, IngredientUnit.PIECE),
                            new IngredientInputDTO("milk", 250.0, IngredientUnit.ML),
                            new IngredientInputDTO("ice cubes", 4.0, IngredientUnit.PIECE)
                    ))
                    .steps(List.of(
                            "Peel and chop the fruit.",
                            "Add all ingredients to a blender.",
                            "Blend until smooth and serve cold."
                    ))
                    .build()
    );

    private TempData() {
    }
}
