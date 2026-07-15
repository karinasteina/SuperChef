-- Fix the favorite_recipe_id column to allow NULL for auto-generation
-- This is needed because the existing schema has it as NOT NULL
DROP TABLE IF EXISTS favorite_recipe;

CREATE TABLE favorite_recipe (
    favorite_recipe_id INTEGER PRIMARY KEY AUTOINCREMENT,
    profile_id INTEGER NOT NULL,
    recipe_id INTEGER NOT NULL,
    FOREIGN KEY (profile_id) REFERENCES Profile(ProfileId),
    FOREIGN KEY (recipe_id) REFERENCES recipes(id),
    CONSTRAINT uk_favorite_profile_recipe UNIQUE (profile_id, recipe_id)
);