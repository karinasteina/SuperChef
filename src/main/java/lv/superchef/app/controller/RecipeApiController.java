package lv.superchef.app.controller;


import jakarta.validation.Valid;
import lv.superchef.app.dto.RecipeCreateDTO;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.service.IRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = "*") // so frontend can talk to this endpoint locally
public class RecipeApiController
{

    @Autowired
    private IRecipeService recipeService;


    @PostMapping
    public ResponseEntity<Recipe> createPost(@Valid @RequestBody RecipeCreateDTO dto)
    {
        return ResponseEntity.ok(recipeService.createRecipe(dto));
    }

    @GetMapping
    public ResponseEntity<List<Recipe>> getAllPosts()
    {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id)
    {
        return ResponseEntity.ok(recipeService.getRecipeById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Recipe>> searchRecipes(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) Integer maxCalories,
            @RequestParam(required = false) Integer maxPrepTime,
            @RequestParam(required = false) Integer maxCookTime
    )
    {
        return ResponseEntity.ok(recipeService.searchRecipes(keyword, category, difficulty, maxCalories, maxPrepTime, maxCookTime));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @Valid @RequestBody RecipeCreateDTO dto)
    {
        return ResponseEntity.ok(recipeService.updateRecipe(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id)
    {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }
}