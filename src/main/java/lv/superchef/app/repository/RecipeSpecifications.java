package lv.superchef.app.repository;

import lv.superchef.app.model.Recipe;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class RecipeSpecifications {
    public static Specification<Recipe> recipeByFilter(String keyword, String category,
                                                       String difficulty, Integer maxCalories,
                                                       Integer maxPrepTime, Integer maxCookTime) {
      return (root, query, cb) -> {
          List<Predicate> predicates = new ArrayList<>();

          if(keyword != null && !keyword.isBlank()){
              String pattern = "%" + keyword.toLowerCase() + "%";
              predicates.add(cb.or(
                      cb.like(cb.lower(root.get("title")), pattern),
                      cb.like(cb.lower(root.get("description")), pattern)
              ));
          }

          if(category != null && !category.isBlank()){
              predicates.add(cb.equal(root.get("category"), category));
          }

          if(difficulty != null && !difficulty.isBlank()){
              predicates.add(cb.equal(root.get("difficulty"), difficulty));
          }

          if(maxCalories != null){
              predicates.add(cb.lessThanOrEqualTo(root.get("calories"), maxCalories));
          }

          if(maxPrepTime != null){
              predicates.add(cb.lessThanOrEqualTo(root.get("preparationTime"), maxPrepTime));
          }

          if(maxCookTime != null){
              predicates.add(cb.lessThanOrEqualTo(root.get("cookingTime"), maxCookTime));
          }

          return cb.and(predicates.toArray(new Predicate[0]));
      };
    }
}
