-- WARNING: These DROP statements delete all favorites and reviews.

DROP TABLE IF EXISTS favorite_recipe;

CREATE TABLE favorite_recipe (
                                 favorite_recipe_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                 profile_id INTEGER NOT NULL,
                                 recipe_id INTEGER NOT NULL,

                                 CONSTRAINT fk_favorite_profile
                                     FOREIGN KEY (profile_id)
                                         REFERENCES Profile(ProfileId),

                                 CONSTRAINT fk_favorite_recipe
                                     FOREIGN KEY (recipe_id)
                                         REFERENCES recipes(id),

                                 CONSTRAINT uk_favorite_profile_recipe
                                     UNIQUE (profile_id, recipe_id)
);

DROP TABLE IF EXISTS review;

CREATE TABLE review (
                         review_id INTEGER PRIMARY KEY AUTOINCREMENT,
                         profile_id INTEGER NOT NULL,
                         recipe_id INTEGER NOT NULL,
                         comment VARCHAR(2000) NOT NULL,
                         rating INTEGER NOT NULL
                             CHECK (rating BETWEEN 1 AND 5),
                         created_at TIMESTAMP NOT NULL,
                         updated_at TIMESTAMP NOT NULL,

                         CONSTRAINT uk_review_profile_recipe
                             UNIQUE (profile_id, recipe_id),

                         CONSTRAINT fk_review_profile
                             FOREIGN KEY (profile_id)
                                 REFERENCES Profile(ProfileId),

                         CONSTRAINT fk_review_recipe
                             FOREIGN KEY (recipe_id)
                                 REFERENCES recipes(id)
);