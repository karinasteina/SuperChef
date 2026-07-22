package lv.superchef.app.repository;

import lv.superchef.app.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface IRecipeRepo extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {
    boolean existsByTitle(String title);

    List<Recipe> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);

    List<Recipe> findByAuthor_Id(Long profileId);

    List<Recipe> findByAuthor_IdIn(List<Long> profileIds);
}
