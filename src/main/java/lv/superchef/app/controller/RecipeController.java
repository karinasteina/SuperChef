package lv.superchef.app.controller;

import lv.superchef.app.dto.CreateRecipeDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static lv.superchef.app.config.TempData.RECIPE_DATA;

@Controller
@RequestMapping("/recipes")
public class RecipeController {

    @GetMapping
    public String listOfRecipes(Model model) {
        model.addAttribute("activePage", "recipes");
        model.addAttribute("recipes", RECIPE_DATA);
        return "recipe/list";
    }

    @GetMapping("/{id}")
    public String recipeDetails(@PathVariable int id, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("recipe", RECIPE_DATA.get(id));
        return "recipe/details";
    }

    @GetMapping("/create")
    public String showCreateRecipePage(Model model) {
        model.addAttribute("activePage", "createRecipe");
        model.addAttribute("createRecipeDto", new CreateRecipeDto());
        model.addAttribute("ingredientUnits", IngredientUnit.values());
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
