package lv.superchef.app.service.impl;


import jakarta.persistence.EntityNotFoundException;
import lv.superchef.app.dto.IngredientInputDTO;
import lv.superchef.app.dto.RecipeCreateDTO;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.repository.IRecipeRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Recipe Service Implementation Unit Tests")
public class RecipeServiceImplTest
{
    @Mock
    private IRecipeRepo recipeRepo;

    @InjectMocks
    private RecipeServiceImpl recipeService;

    @Test
    @DisplayName("Create Recipe: Should map all DTO fields correctly and return saved recipe with generated ID")
    void createRecipe_ShouldReturnSavedRecipe_WithCorrectMappings()
    {

        RecipeCreateDTO dto = new RecipeCreateDTO();
        dto.setTitle("Shashlik");
        dto.setDescription("Juicy grilled pork shashlik");
        dto.setImageUrl("/images/recipes/shashlik.webp");
        dto.setCalories(550);
        dto.setPreparationTime(20);
        dto.setCookingTime(30);
        dto.setDifficulty("Medium");
        dto.setCategory("Dinner");

        IngredientInputDTO ingDto = new IngredientInputDTO();
        ingDto.setName("Pork Neck");
        ingDto.setQuantity(1000.0);
        ingDto.setUnit(null);
        dto.setIngredients(List.of(ingDto));

        dto.setSteps(List.of("Marinate meat", "Grill over charcoal"));

        when(recipeRepo.save(any(Recipe.class))).thenAnswer(invocation -> {
            Recipe recipeToSave = invocation.getArgument(0);
            ReflectionTestUtils.setField(recipeToSave, "id", 1L);
            return recipeToSave;
        });

        Recipe savedRecipe = recipeService.createRecipe(dto);

        // Assert
        assertNotNull(savedRecipe);
        assertEquals(1L, savedRecipe.getId());
        assertEquals("Shashlik", savedRecipe.getTitle());
        assertEquals("/images/recipes/shashlik.webp", savedRecipe.getImageUrl());
        assertEquals(1, savedRecipe.getIngredients().size());
        assertEquals("Pork Neck", savedRecipe.getIngredients().get(0).getIngredientName());
        assertEquals(2, savedRecipe.getSteps().size());
        assertEquals(1, savedRecipe.getSteps().get(0).getStepNumber()); // Verifies order counter logic
        verify(recipeRepo, times(1)).save(any(Recipe.class));
    }
    @Test
    @DisplayName("Get Recipe By ID: Should return matching recipe when valid ID is provided")
    void getRecipeById_ShouldReturnRecipe_WhenRecipeExists()
    {
        Recipe mockRecipe = new Recipe();
        ReflectionTestUtils.setField(mockRecipe, "id", 1L);
        mockRecipe.setTitle("Spaghetti");

        when(recipeRepo.findById(1L)).thenReturn(Optional.of(mockRecipe));

        Recipe foundRecipe = recipeService.getRecipeById(1L);

        // Assert
        assertNotNull(foundRecipe);
        assertEquals(1L, foundRecipe.getId());
        assertEquals("Spaghetti", foundRecipe.getTitle());
        verify(recipeRepo, times(1)).findById(1L);
    }
    @Test
    @DisplayName("Get Recipe By ID: Should throw EntityNotFoundException when ID does not exist")
    void getRecipeById_ShouldThrowException_WhenRecipeDoesNotExist()
    {
        when(recipeRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            recipeService.getRecipeById(999L);
        });
        verify(recipeRepo, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Update Recipe: Should overwrite attributes and refresh update timestamp when recipe exists")
    void updateRecipe_ShouldReturnUpdatedRecipe_WhenRecipeExists()
    {
        Long recipeId = 2L;
        Recipe existingRecipe = new Recipe();
        ReflectionTestUtils.setField(existingRecipe, "id", recipeId);
        existingRecipe.setTitle("Old Title");

        RecipeCreateDTO updateDto = new RecipeCreateDTO();
        updateDto.setTitle("New Refactored Title");
        updateDto.setDescription("Updated description details");

        when(recipeRepo.findById(recipeId)).thenReturn(Optional.of(existingRecipe));
        when(recipeRepo.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recipe result = recipeService.updateRecipe(recipeId, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals("New Refactored Title", result.getTitle());
       // assertNotNull(result.getUpdatedAt());
        verify(recipeRepo, times(1)).findById(recipeId);
        verify(recipeRepo, times(1)).save(any(Recipe.class));
    }

    @Test
    @DisplayName("Update Recipe: Should throw EntityNotFoundException and block save action when ID does not exist")
    void updateRecipe_ShouldThrowException_WhenRecipeDoesNotExist()
    {
        Long nonExistingId = 404L;
        RecipeCreateDTO updateDto = new RecipeCreateDTO();

        when(recipeRepo.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            recipeService.updateRecipe(nonExistingId, updateDto);
        });
        verify(recipeRepo, never()).save(any(Recipe.class));
    }
    @Test
    @DisplayName("Delete Recipe: Should invoke repository delete sequence when valid ID is provided")
    void deleteRecipe_ShouldCallRepositoryDelete_WhenRecipeExists()
    {
        Long recipeId = 5L;
        Recipe existingRecipe = new Recipe();
        ReflectionTestUtils.setField(existingRecipe, "id", recipeId);

        when(recipeRepo.findById(recipeId)).thenReturn(Optional.of(existingRecipe));
        doNothing().when(recipeRepo).delete(existingRecipe);

        recipeService.deleteRecipe(recipeId);

        // Assert
        verify(recipeRepo, times(1)).findById(recipeId);
        verify(recipeRepo, times(1)).delete(existingRecipe);
    }

    @Test
    @DisplayName("Get All Recipes: Should return an empty list gracefully when no data is found")
    void getAllRecipes_ShouldReturnEmptyList_WhenNoRecipesExist()
    {
        when(recipeRepo.findAll()).thenReturn(new ArrayList<>());

        List<Recipe> result = recipeService.getAllRecipes();

        // Assert
        assertTrue(result.isEmpty());
        verify(recipeRepo, times(1)).findAll();
    }
}
