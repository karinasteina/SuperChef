package lv.superchef.app.repository;

import lv.superchef.app.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IRecipeRepo extends JpaRepository<Recipe, Long> {
    List<Recipe> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
}
