CREATE UNIQUE INDEX IF NOT EXISTS uk_favorite_profile_recipe
    ON favorite_recipe (profile_id, recipe_id);