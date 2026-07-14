package lv.superchef.app.repository;

import lv.superchef.app.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRecipeRepo extends JpaRepository<Recipe, Long> {
    Optional<Recipe> findByTitle(String title);
}
