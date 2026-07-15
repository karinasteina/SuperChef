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
public class RecipeApiController {

    @Autowired
    private IRecipeService recipeService;

    @PostMapping
    public ResponseEntity<Recipe> createPost(@Valid @RequestBody RecipeCreateDTO dto) {
        return ResponseEntity.ok(recipeService.createRecipe(dto));
    }

    @GetMapping
    public ResponseEntity<List<Recipe>> getAllPosts() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }
}
