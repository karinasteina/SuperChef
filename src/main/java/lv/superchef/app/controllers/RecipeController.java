package lv.superchef.app.controllers;


import lv.superchef.app.dtos.RecipeCreateDTO;
import lv.superchef.app.models.Recipe;
import lv.superchef.app.services.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = "*") // so frontend can talk to this endpoint locally
public class RecipeController
{

    @Autowired
    private RecipeService recipeService;

    @PostMapping
    public ResponseEntity<Recipe> createPost(@RequestBody RecipeCreateDTO dto)
    {
        return ResponseEntity.ok(recipeService.createRecipe(dto));
    }

    @GetMapping
    public ResponseEntity<List<Recipe>> getAllPosts()
    {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }
}
