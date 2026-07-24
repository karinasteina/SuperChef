package lv.superchef.app.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lv.superchef.app.dto.IngredientInputDTO;
import lv.superchef.app.dto.RecipeCreateDTO;
import lv.superchef.app.model.Follow;
import lv.superchef.app.model.Profile;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.repository.IFollowRepo;
import lv.superchef.app.repository.IProfileRepo;
import lv.superchef.app.repository.IRecipeRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Recipe Service Implementation Unit Tests")
public class RecipeServiceImplTest
{
    @Mock
    private IRecipeRepo recipeRepo;

    @Mock
    private IProfileRepo profileRepo;

    @Mock
    private IFollowRepo followRepo;

    @InjectMocks
    private RecipeServiceImpl recipeService;

    @Nested
    @DisplayName("createRecipe Tests")
    class CreateRecipeTests
    {
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

            assertNotNull(savedRecipe);
            assertEquals(1L, savedRecipe.getId());
            assertEquals("Shashlik", savedRecipe.getTitle());
            assertEquals("/images/recipes/shashlik.webp", savedRecipe.getImageUrl());
            assertEquals(1, savedRecipe.getIngredients().size());
            assertEquals("Pork Neck", savedRecipe.getIngredients().get(0).getIngredientName());
            assertEquals(2, savedRecipe.getSteps().size());
            assertEquals(1, savedRecipe.getSteps().get(0).getStepNumber());
            verify(recipeRepo, times(1)).save(any(Recipe.class));
        }

        @Test
        @DisplayName("Create Recipe: Should attach author profile when authorUserId is provided and exists")
        void createRecipe_WithAuthorUserId_ShouldAttachAuthor()
        {
            Long userId = 10L;
            RecipeCreateDTO dto = new RecipeCreateDTO();
            dto.setTitle("Chicken Mango");
            dto.setAuthorUserId(userId);

            Profile profile = new Profile();
            ReflectionTestUtils.setField(profile, "id", 100L);

            when(profileRepo.findByAppUser_Id(userId)).thenReturn(Optional.of(profile));
            when(recipeRepo.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Recipe savedRecipe = recipeService.createRecipe(dto);

            assertNotNull(savedRecipe);
            assertNotNull(savedRecipe.getAuthor());
            assertEquals(100L, savedRecipe.getAuthor().getId());
            verify(profileRepo, times(1)).findByAppUser_Id(userId);
            verify(recipeRepo, times(1)).save(any(Recipe.class));
        }

        @Test
        @DisplayName("Create Recipe: Should throw EntityNotFoundException when authorUserId profile does not exist")
        void createRecipe_WithNonExistentAuthorUserId_ShouldThrowException()
        {
            Long userId = 99L;
            RecipeCreateDTO dto = new RecipeCreateDTO();
            dto.setTitle("Burger");
            dto.setAuthorUserId(userId);

            when(profileRepo.findByAppUser_Id(userId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> recipeService.createRecipe(dto));
            verify(profileRepo, times(1)).findByAppUser_Id(userId);
            verify(recipeRepo, never()).save(any(Recipe.class));
        }
    }

    @Nested
    @DisplayName("getRecipeById & getAllRecipes Tests")
    class ReadRecipeTests
    {
        @Test
        @DisplayName("Get Recipe By ID: Should return matching recipe when valid ID is provided")
        void getRecipeById_ShouldReturnRecipe_WhenRecipeExists()
        {
            Recipe mockRecipe = new Recipe();
            ReflectionTestUtils.setField(mockRecipe, "id", 1L);
            mockRecipe.setTitle("Spaghetti");

            when(recipeRepo.findById(1L)).thenReturn(Optional.of(mockRecipe));

            Recipe foundRecipe = recipeService.getRecipeById(1L);

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

            assertThrows(EntityNotFoundException.class, () -> recipeService.getRecipeById(999L));
            verify(recipeRepo, times(1)).findById(999L);
        }

        @Test
        @DisplayName("Get All Recipes: Should return an empty list gracefully when no data is found")
        void getAllRecipes_ShouldReturnEmptyList_WhenNoRecipesExist()
        {
            when(recipeRepo.findAll()).thenReturn(new ArrayList<>());

            List<Recipe> result = recipeService.getAllRecipes();

            assertTrue(result.isEmpty());
            verify(recipeRepo, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("updateRecipe & deleteRecipe Tests")
    class UpdateAndDeleteRecipeTests
    {
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

            assertNotNull(result);
            assertEquals("New Refactored Title", result.getTitle());
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

            assertThrows(EntityNotFoundException.class, () -> recipeService.updateRecipe(nonExistingId, updateDto));
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

            verify(recipeRepo, times(1)).findById(recipeId);
            verify(recipeRepo, times(1)).delete(existingRecipe);
        }
    }

    @Nested
    @DisplayName("searchRecipes Tests")
    class SearchRecipesTests
    {
        @Test
        @DisplayName("Search Recipes (6-param): Should execute repository findAll with specification")
        void searchRecipes_WithoutLimit_ShouldCallFindAllWithSpec()
        {
            List<Recipe> expected = List.of(new Recipe());
            when(recipeRepo.findAll(any(Specification.class))).thenReturn(expected);

            List<Recipe> result = recipeService.searchRecipes("Pork", "Dinner", "Easy", 500, 15, 30);

            assertEquals(expected, result);
            verify(recipeRepo, times(1)).findAll(any(Specification.class));
        }

        @Test
        @DisplayName("searchRecipes (7-param): Should apply limit when limit is a positive integer (> 0)")
        @SuppressWarnings({"unchecked", "rawtypes"})
        void searchRecipes_WithPositiveLimit_AppliesLimitToQuery()
        {
            Recipe recipe = new Recipe();
            recipe.setTitle("Grilled Shashlik");
            List<Recipe> expected = List.of(recipe);

            when(recipeRepo.findBy(any(Specification.class), any())).thenAnswer(invocation -> {
                Function queryFunction = invocation.getArgument(1);

                JpaSpecificationExecutor.SpecificationFluentQuery mockQuery =
                        mock(JpaSpecificationExecutor.SpecificationFluentQuery.class);

                when(mockQuery.limit(5)).thenReturn(mockQuery);
                when(mockQuery.all()).thenReturn(expected);

                return queryFunction.apply(mockQuery);
            });

            List<Recipe> result = recipeService.searchRecipes("Pork", "Dinner", "Medium", 600, 30, 45, 5);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Grilled Shashlik", result.get(0).getTitle());
            verify(recipeRepo, times(1)).findBy(any(Specification.class), any());
        }

        @Test
        @DisplayName("searchRecipes (7-param): Should fetch all without limit when limit is null")
        @SuppressWarnings({"unchecked", "rawtypes"})
        void searchRecipes_WithNullLimit_FetchesAllWithoutLimit()
        {
            List<Recipe> expected = List.of(new Recipe());

            when(recipeRepo.findBy(any(Specification.class), any())).thenAnswer(invocation -> {
                Function queryFunction = invocation.getArgument(1);

                JpaSpecificationExecutor.SpecificationFluentQuery mockQuery =
                        mock(JpaSpecificationExecutor.SpecificationFluentQuery.class);

                when(mockQuery.all()).thenReturn(expected);

                return queryFunction.apply(mockQuery);
            });

            List<Recipe> result = recipeService.searchRecipes("Pork", null, null, null, null, null, null);

            assertEquals(expected, result);
            verify(recipeRepo, times(1)).findBy(any(Specification.class), any());
        }

        @Test
        @DisplayName("searchRecipes (7-param): Should fetch all without limit when limit is zero or negative")
        @SuppressWarnings({"unchecked", "rawtypes"})
        void searchRecipes_WithZeroOrNegativeLimit_FetchesAllWithoutLimit()
        {
            List<Recipe> expected = List.of(new Recipe());

            when(recipeRepo.findBy(any(Specification.class), any())).thenAnswer(invocation -> {
                Function queryFunction = invocation.getArgument(1);

                JpaSpecificationExecutor.SpecificationFluentQuery mockQuery =
                        mock(JpaSpecificationExecutor.SpecificationFluentQuery.class);

                when(mockQuery.all()).thenReturn(expected);

                return queryFunction.apply(mockQuery);
            });

            List<Recipe> resultZero = recipeService.searchRecipes("Chicken", null, null, null, null, null, 0);
            List<Recipe> resultNegative = recipeService.searchRecipes("Chicken", null, null, null, null, null, -10);

            assertEquals(expected, resultZero);
            assertEquals(expected, resultNegative);
            verify(recipeRepo, times(2)).findBy(any(Specification.class), any());
        }
    }

    @Nested
    @DisplayName("getRecipesByFollowedProfilesForUser Tests")
    class FollowedRecipesTests
    {

        @Test
        @DisplayName("Get Followed Recipes: Should throw IllegalArgumentException when userId is null")
        void getRecipesByFollowedProfilesForUser_NullUserId_ShouldThrowException()
        {
            assertThrows(IllegalArgumentException.class, () ->
                    recipeService.getRecipesByFollowedProfilesForUser(null)
            );
        }

        @Test
        @DisplayName("Get Followed Recipes: Should throw EntityNotFoundException when user profile does not exist")
        void getRecipesByFollowedProfilesForUser_ProfileNotFound_ShouldThrowException()
        {
            Long userId = 1L;
            when(profileRepo.findByAppUser_Id(userId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () ->
                    recipeService.getRecipesByFollowedProfilesForUser(userId)
            );
            verify(profileRepo, times(1)).findByAppUser_Id(userId);
        }

        @Test
        @DisplayName("Get Followed Recipes: Should return empty list when user follows no one")
        void getRecipesByFollowedProfilesForUser_NoFollowedUsers_ShouldReturnEmptyList()
        {
            Long userId = 1L;
            Long profileId = 10L;

            Profile profile = new Profile();
            ReflectionTestUtils.setField(profile, "id", profileId);

            when(profileRepo.findByAppUser_Id(userId)).thenReturn(Optional.of(profile));
            when(followRepo.findByFollower_Id(profileId)).thenReturn(List.of());

            List<Recipe> result = recipeService.getRecipesByFollowedProfilesForUser(userId);

            assertTrue(result.isEmpty());
            verify(recipeRepo, never()).findByAuthor_IdIn(any());
        }

        @Test
        @DisplayName("Get Followed Recipes: Should return recipes from followed authors when user follows profiles")
        void getRecipesByFollowedProfilesForUser_WithFollowedUsers_ShouldReturnRecipes()
        {
            Long userId = 1L;
            Long profileId = 10L;
            Long followedProfileId = 20L;

            Profile userProfile = new Profile();
            ReflectionTestUtils.setField(userProfile, "id", profileId);

            Profile followedProfile = new Profile();
            ReflectionTestUtils.setField(followedProfile, "id", followedProfileId);

            Follow follow = new Follow();
            follow.setFollower(userProfile);
            follow.setFollowing(followedProfile);

            Recipe followedRecipe = new Recipe();
            followedRecipe.setTitle("Followed User's Recipe");

            when(profileRepo.findByAppUser_Id(userId)).thenReturn(Optional.of(userProfile));
            when(followRepo.findByFollower_Id(profileId)).thenReturn(List.of(follow));
            when(recipeRepo.findByAuthor_IdIn(List.of(followedProfileId))).thenReturn(List.of(followedRecipe));

            List<Recipe> result = recipeService.getRecipesByFollowedProfilesForUser(userId);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Followed User's Recipe", result.get(0).getTitle());
            verify(recipeRepo, times(1)).findByAuthor_IdIn(List.of(followedProfileId));
        }
    }
}