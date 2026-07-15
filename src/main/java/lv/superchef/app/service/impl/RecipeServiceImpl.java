package lv.superchef.app.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lv.superchef.app.dto.IngredientInputDTO;
import lv.superchef.app.dto.RecipeCreateDTO;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.model.RecipeIngredient;
import lv.superchef.app.model.RecipeStep;
import lv.superchef.app.repository.IRecipeRepo;
import lv.superchef.app.repository.RecipeSpecifications;
import lv.superchef.app.service.IRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;


@Service
public class RecipeServiceImpl implements IRecipeService {
    private static final String DEFAULT_IMAGE_URL = "/images/recipes/recipe-00.webp";
    @Autowired
    private IRecipeRepo recipeRepo;


    @Override
    public List<Recipe> searchRecipes(String keyword, String category, String difficulty, Integer maxCalories, Integer maxPrepTime, Integer maxCookTime) {
        return recipeRepo.findAll(RecipeSpecifications.recipeByFilter(keyword, category,
                difficulty, maxCalories, maxPrepTime, maxCookTime));
    }

    @Override
    public Recipe getRecipeById(Long id) {
        return recipeRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Recipe not found: " + id));
    }

    @Override
    public Recipe createRecipe(RecipeCreateDTO dto, MultipartFile coverImage) {
        Recipe recipe = new Recipe();
        recipe.setTitle(dto.getTitle());
        recipe.setDescription(dto.getDescription());
        recipe.setCalories(dto.getCalories());
        recipe.setPreparationTime(dto.getPreparationTime());
        recipe.setCookingTime(dto.getCookingTime());
        recipe.setDifficulty(dto.getDifficulty());
        recipe.setCategory(dto.getCategory());
        recipe.setImageUrl(storeCoverImage(coverImage));

        if (dto.getIngredients() != null) {
            for (IngredientInputDTO ingredientDto : dto.getIngredients()) {
                if (ingredientDto == null || ingredientDto.getName() == null || ingredientDto.getName().isBlank()) {
                    continue;
                }

                RecipeIngredient ingredient = new RecipeIngredient();
                ingredient.setIngredientName(ingredientDto.getName().trim());
                ingredient.setQuantity(ingredientDto.getQuantity());
                ingredient.setUnit(ingredientDto.getUnit());
                recipe.getIngredients().add(ingredient);
            }
        }

        if (dto.getSteps() != null) {
            int stepNumber = 1;
            for (String instruction : dto.getSteps()) {
                if (instruction == null || instruction.isBlank()) {
                    continue;
                }

                RecipeStep step = new RecipeStep();
                step.setStepNumber(stepNumber++);
                step.setInstruction(instruction.trim());
                recipe.getSteps().add(step);
            }
        }

        return recipeRepo.save(recipe);
    }

    @Override
    public List<Recipe> getAllRecipes() {
        return recipeRepo.findAll();
    }

    private String storeCoverImage(MultipartFile coverImage) {
        if (coverImage == null || coverImage.isEmpty()) {
            return DEFAULT_IMAGE_URL;
        }

        String extension = getExtension(coverImage.getOriginalFilename());
        String fileName = UUID.randomUUID() + extension;

        Path targetDir = Paths.get("src/main/resources/static/images/recipes").toAbsolutePath().normalize();
        Path targetFile = targetDir.resolve(fileName).normalize();

        try {
            Files.createDirectories(targetDir);
            Files.copy(coverImage.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not store recipe cover image", ex);
        }

        return "/images/recipes/" + fileName;
    }

    private String getExtension(String fileName) {
        if (fileName == null) {
            return ".webp";
        }

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return ".webp";
        }

        return fileName.substring(dotIndex);
    }
}
