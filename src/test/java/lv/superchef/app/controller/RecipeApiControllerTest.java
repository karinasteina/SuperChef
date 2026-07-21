package lv.superchef.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lv.superchef.app.config.SecurityConfig;
import lv.superchef.app.dto.IngredientInputDTO;
import lv.superchef.app.dto.RecipeCreateDTO;
import lv.superchef.app.enums.IngredientUnit;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.service.IRecipeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipeApiController.class)
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@DisplayName("Recipe API Controller Unit Tests")
class RecipeApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private IRecipeService recipeService;

    @Nested
    @DisplayName("GET /api/recipes")
    class GetAllPostsTests {


        @Test
        @DisplayName("Should return 200 OK with list of recipes")
        void getAllPosts_ShouldReturnRecipeList() throws Exception {
            Recipe recipe = new Recipe();
            ReflectionTestUtils.setField(recipe, "id", 1L);
            recipe.setTitle("Korean Popcorn Chicken");

            when(recipeService.getAllRecipes()).thenReturn(List.of(recipe));

            mockMvc.perform(get("/api/recipes")
                            .with(user("testuser").roles("USER"))) // Pass security context directly in the request
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].title").value("Korean Popcorn Chicken"));

            verify(recipeService, times(1)).getAllRecipes();
        }

        @Test
        @DisplayName("Should return 200 OK with empty array when no recipes exist")
        void getAllPosts_EmptyList_ShouldReturnEmptyJsonArray() throws Exception {
            when(recipeService.getAllRecipes()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/recipes")
                            .with(user("testuser").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("POST /api/recipes")
    class CreatePostTests {

        @Test
        @DisplayName("Should return 200 OK and created Recipe when DTO is valid")
        void createPost_ValidDto_ShouldReturnCreatedRecipe() throws Exception {
            RecipeCreateDTO dto = createValidRecipeDTO();

            Recipe savedRecipe = new Recipe();
            ReflectionTestUtils.setField(savedRecipe, "id", 10L);
            savedRecipe.setTitle(dto.getTitle());

            when(recipeService.createRecipe(any(RecipeCreateDTO.class))).thenReturn(savedRecipe);

            mockMvc.perform(post("/api/recipes")
                            .with(csrf())
                            .with(user("testuser").roles("USER"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(10))
                    .andExpect(jsonPath("$.title").value("Oven-Baked Honey Soy Chicken"));

            verify(recipeService, times(1)).createRecipe(any(RecipeCreateDTO.class));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when DTO validation fails (@Valid)")
        void createPost_InvalidDto_ShouldReturn400() throws Exception {
            // Unpopulated DTO triggers @NotBlank, @NotEmpty, and @NotNull validations
            RecipeCreateDTO invalidDto = new RecipeCreateDTO();

            mockMvc.perform(post("/api/recipes")
                            .with(csrf())
                            .with(user("testuser").roles("USER"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDto)))
                    .andExpect(status().isBadRequest());

            verify(recipeService, never()).createRecipe(any());
        }
    }

    private RecipeCreateDTO createValidRecipeDTO() {
        IngredientInputDTO ingredient = new IngredientInputDTO(
                "Chicken Thighs",
                600.0,
                IngredientUnit.G
        );

        return RecipeCreateDTO.builder()
                .title("Oven-Baked Honey Soy Chicken")
                .description("Crispy marinated chicken wok-style in the oven.")
                .calories(550)
                .preparationTime(15)
                .cookingTime(25)
                .difficulty("EASY")
                .category("MAIN_COURSE")
                .ingredients(List.of(ingredient))
                .steps(List.of("Marinate chicken for 30 mins.", "Bake at 200°C until golden."))
                .build();
    }
}