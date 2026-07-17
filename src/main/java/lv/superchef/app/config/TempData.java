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
                    .imageUrl("/images/recipes/recipe-00.webp")
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
                    .imageUrl("/images/recipes/recipe-01.webp")
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
                    .imageUrl("/images/recipes/recipe-02.webp")
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
                    .imageUrl("/images/recipes/recipe-03.webp")
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
                    .imageUrl("/images/recipes/recipe-04.webp")
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
                    .imageUrl("/images/recipes/recipe-05.webp")
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
                    .imageUrl("/images/recipes/recipe-06.webp")
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
                    .imageUrl("/images/recipes/recipe-07.webp")
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
                    .imageUrl("/images/recipes/recipe-08.webp")
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
                    .imageUrl("/images/recipes/recipe-09.webp")
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
                    .build(),

            RecipeCreateDTO.builder()
                    .title("Classic Beef Wellington")
                    .description("Beef Wellington is a showstopping centerpiece built around tender beef fillet. The meat is seared deeply to create a savory crust. A mushroom duxelles adds concentrated earthy flavor. Prosciutto forms a protective layer that helps keep the pastry crisp. A thin crêpe provides another barrier against excess moisture. Dijon mustard brings gentle heat and acidity. Chilled puff pastry wraps every component in a flaky golden shell. Careful resting keeps the beef juicy when sliced. The finished dish rewards precise timing with dramatic layers and a rosy center.")
                    .imageUrl("/images/recipes/recipe-10.webp")
                    .calories(890)
                    .preparationTime(75)
                    .cookingTime(50)
                    .difficulty("Hard")
                    .category("Dinner")
                    .ingredients(List.of(
                            new IngredientInputDTO("center-cut beef tenderloin", 1.0, IngredientUnit.KG),
                            new IngredientInputDTO("salt", 2.0, IngredientUnit.TSP),
                            new IngredientInputDTO("black pepper", 1.0, IngredientUnit.TSP),
                            new IngredientInputDTO("olive oil", 2.0, IngredientUnit.TBSP),
                            new IngredientInputDTO("cremini mushrooms", 500.0, IngredientUnit.G),
                            new IngredientInputDTO("shallots", 2.0, IngredientUnit.PIECE),
                            new IngredientInputDTO("garlic cloves", 2.0, IngredientUnit.CLOVE),
                            new IngredientInputDTO("butter", 30.0, IngredientUnit.G),
                            new IngredientInputDTO("fresh thyme", 1.0, IngredientUnit.TBSP),
                            new IngredientInputDTO("Dijon mustard", 2.0, IngredientUnit.TBSP),
                            new IngredientInputDTO("prosciutto", 10.0, IngredientUnit.SLICE),
                            new IngredientInputDTO("thin crêpes", 2.0, IngredientUnit.PIECE),
                            new IngredientInputDTO("puff pastry", 500.0, IngredientUnit.G),
                            new IngredientInputDTO("egg", 1.0, IngredientUnit.PIECE),
                            new IngredientInputDTO("flour", 2.0, IngredientUnit.TBSP)
                    ))
                    .steps(List.of(
                            "Season the beef tenderloin with salt and pepper, then sear it in hot olive oil until browned on every side.",
                            "Brush the warm beef with Dijon mustard and refrigerate it until completely cold.",
                            "Finely chop the mushrooms, shallots, and garlic, then cook them with butter and thyme until the mixture is dry.",
                            "Spread the mushroom duxelles on a tray and chill it so it will not soften the pastry.",
                            "Overlap the prosciutto on plastic wrap, cover it with the crêpes and duxelles, and place the beef in the center.",
                            "Use the plastic wrap to roll everything tightly around the beef, then chill the wrapped cylinder for thirty minutes.",
                            "Roll the puff pastry on a lightly floured surface and wrap it snugly around the unwrapped beef cylinder.",
                            "Seal the pastry edges, brush with beaten egg, score a decorative pattern, and chill for another twenty minutes.",
                            "Bake at 200 degrees Celsius until the pastry is golden and the center reaches the desired temperature.",
                            "Rest the Wellington for fifteen minutes before cutting it into thick slices with a serrated knife."
                    ))
                    .build(),

            RecipeCreateDTO.builder()
                    .title("Saffron Seafood Paella")
                    .description("This seafood paella builds layers of flavor in a wide, shallow pan. Saffron perfumes the rice with its distinctive floral aroma. Smoked paprika gives the broth warmth and depth. Chicken and squid provide a savory foundation before the rice is added. Shrimp and mussels cook gently across the surface. The uncovered pan allows each grain to remain separate and flavorful. High heat at the end creates the prized crisp layer called socarrat. A final rest lets the broth settle evenly through the rice. Lemon and parsley brighten the rich seafood finish.")
                    .imageUrl("/images/recipes/recipe-11.webp")
                    .calories(760)
                    .preparationTime(45)
                    .cookingTime(55)
                    .difficulty("Hard")
                    .category("Dinner")
                    .ingredients(List.of(
                            new IngredientInputDTO("bomba rice", 400.0, IngredientUnit.G),
                            new IngredientInputDTO("chicken thighs", 4.0, IngredientUnit.PIECE),
                            new IngredientInputDTO("large shrimp", 300.0, IngredientUnit.G),
                            new IngredientInputDTO("mussels", 500.0, IngredientUnit.G),
                            new IngredientInputDTO("cleaned squid", 250.0, IngredientUnit.G),
                            new IngredientInputDTO("seafood stock", 1.2, IngredientUnit.L),
                            new IngredientInputDTO("saffron threads", 100.0, IngredientUnit.MG),
                            new IngredientInputDTO("olive oil", 4.0, IngredientUnit.TBSP),
                            new IngredientInputDTO("onion", 1.0, IngredientUnit.PIECE),
                            new IngredientInputDTO("red bell pepper", 1.0, IngredientUnit.PIECE),
                            new IngredientInputDTO("garlic cloves", 4.0, IngredientUnit.CLOVE),
                            new IngredientInputDTO("crushed tomatoes", 200.0, IngredientUnit.G),
                            new IngredientInputDTO("smoked paprika", 2.0, IngredientUnit.TSP),
                            new IngredientInputDTO("green beans", 150.0, IngredientUnit.G),
                            new IngredientInputDTO("frozen peas", 100.0, IngredientUnit.G),
                            new IngredientInputDTO("dry white wine", 100.0, IngredientUnit.ML),
                            new IngredientInputDTO("lemon", 1.0, IngredientUnit.PIECE),
                            new IngredientInputDTO("flat-leaf parsley", 1.0, IngredientUnit.BUNCH),
                            new IngredientInputDTO("salt", 1.0, IngredientUnit.TSP)
                    ))
                    .steps(List.of(
                            "Warm the seafood stock, steep the saffron in it, and keep the mixture just below a simmer.",
                            "Season the chicken and brown it in olive oil in a wide paella pan, then move it to the outer edge.",
                            "Sear the squid briefly in the center of the pan and transfer it to a plate before it turns tough.",
                            "Cook the onion, bell pepper, garlic, and green beans until softened and lightly caramelized.",
                            "Stir in the tomatoes and smoked paprika, then reduce the mixture until it becomes a thick sofrito.",
                            "Deglaze with white wine, return the chicken, and distribute the rice evenly across the pan.",
                            "Pour in the saffron stock and simmer without stirring, rotating the pan occasionally for even heat.",
                            "Arrange the shrimp, mussels, squid, and peas over the rice when most of the liquid has been absorbed.",
                            "Cook until the mussels open, then briefly raise the heat to form a crisp socarrat on the bottom.",
                            "Remove the pan from the heat, cover it loosely for ten minutes, and serve with parsley and lemon wedges."
                    ))
                    .build(),

            RecipeCreateDTO.builder()
                    .title("Caramel Croquembouche")
                    .description("Croquembouche is a celebratory tower of crisp cream puffs bound with caramel. Each choux bun must rise into a light shell with a hollow center. Silky vanilla pastry cream gives the dessert its luxurious filling. Dark chocolate adds a subtle bitter note beneath the sweetness. Hot caramel acts as both glue and decoration. Building the cone requires patience because every puff must support the next layer. Fine caramel threads create the traditional spun-sugar finish. The finished tower balances delicate pastry with a dramatic architectural shape. It is best assembled shortly before serving so the shells remain crisp. Guests can pull away individual puffs for an impressive shared dessert.")
                    .imageUrl("/images/recipes/recipe-12.webp")
                    .calories(680)
                    .preparationTime(120)
                    .cookingTime(60)
                    .difficulty("Hard")
                    .category("Dessert")
                    .ingredients(List.of(
                            new IngredientInputDTO("water", 250.0, IngredientUnit.ML),
                            new IngredientInputDTO("whole milk", 500.0, IngredientUnit.ML),
                            new IngredientInputDTO("unsalted butter", 120.0, IngredientUnit.G),
                            new IngredientInputDTO("all-purpose flour", 160.0, IngredientUnit.G),
                            new IngredientInputDTO("eggs", 7.0, IngredientUnit.PIECE),
                            new IngredientInputDTO("salt", 1.0, IngredientUnit.PINCH),
                            new IngredientInputDTO("granulated sugar", 500.0, IngredientUnit.G),
                            new IngredientInputDTO("egg yolks", 5.0, IngredientUnit.PIECE),
                            new IngredientInputDTO("cornstarch", 40.0, IngredientUnit.G),
                            new IngredientInputDTO("vanilla bean", 1.0, IngredientUnit.PIECE),
                            new IngredientInputDTO("dark chocolate", 100.0, IngredientUnit.G),
                            new IngredientInputDTO("heavy cream", 100.0, IngredientUnit.ML),
                            new IngredientInputDTO("glucose syrup", 2.0, IngredientUnit.TBSP)
                    ))
                    .steps(List.of(
                            "Bring the water, butter, and salt to a boil, then add the flour and beat until a smooth dough leaves the pan sides.",
                            "Cool the dough slightly and beat in four eggs one at a time until the choux paste is glossy and pipeable.",
                            "Pipe small even mounds onto lined baking trays and smooth their peaks with a damp fingertip.",
                            "Bake the choux buns until deeply golden, then pierce each base and return them to the turned-off oven to dry.",
                            "Heat the milk with the split vanilla bean until steaming and allow it to infuse for ten minutes.",
                            "Whisk three eggs, the egg yolks, 150 grams of sugar, and cornstarch, then temper the mixture with the hot milk.",
                            "Cook the pastry cream until thick and bubbling, chill it completely, and pipe it into every cooled choux bun.",
                            "Heat the heavy cream and pour it over the dark chocolate, then stir until smooth to make a ganache.",
                            "Dip the filled puffs lightly in ganache and let the chocolate set before beginning the tower.",
                            "Cook the remaining sugar with the glucose syrup to a deep amber caramel without stirring.",
                            "Dip each puff carefully in caramel and build concentric layers around a cone, narrowing each layer toward the top.",
                            "Use a fork to wind fine caramel threads around the finished tower and serve as soon as the caramel hardens."
                    ))
                    .build()
    );

    private TempData() {
    }
}
