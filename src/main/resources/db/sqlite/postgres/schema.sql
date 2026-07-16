

DROP TABLE IF EXISTS favorite_recipe CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;


DROP SEQUENCE IF EXISTS favorite_recipe_id_seq;
DROP SEQUENCE IF EXISTS reviews_id_seq;




CREATE SEQUENCE favorite_recipe_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;



CREATE TABLE favorite_recipe (
                                 favorite_recipe_id BIGINT NOT NULL
                                     DEFAULT nextval('favorite_recipe_id_seq'),

                                 profile_id BIGINT NOT NULL,
                                 recipe_id BIGINT NOT NULL,

                                 CONSTRAINT pk_favorite_recipe
                                     PRIMARY KEY (favorite_recipe_id),

                                 CONSTRAINT uk_favorite_profile_recipe
                                     UNIQUE (profile_id, recipe_id),

                                 CONSTRAINT fk_favorite_profile
                                     FOREIGN KEY (profile_id)
                                         REFERENCES profile(profileid)
                                         ON DELETE CASCADE,

                                 CONSTRAINT fk_favorite_recipe
                                     FOREIGN KEY (recipe_id)
                                         REFERENCES recipes(id)
                                         ON DELETE CASCADE
);

ALTER SEQUENCE favorite_recipe_id_seq
    OWNED BY favorite_recipe.favorite_recipe_id;



CREATE SEQUENCE reviews_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


CREATE TABLE reviews (
                         review_id BIGINT NOT NULL
                             DEFAULT nextval('reviews_id_seq'),

                         profile_id BIGINT NOT NULL,
                         recipe_id BIGINT NOT NULL,

                         comment VARCHAR(2000) NOT NULL,
                         rating INTEGER NOT NULL,

                         created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                         updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,

                         CONSTRAINT pk_reviews
                             PRIMARY KEY (review_id),

                         CONSTRAINT uk_review_profile_recipe
                             UNIQUE (profile_id, recipe_id),

                         CONSTRAINT chk_review_rating
                             CHECK (rating BETWEEN 1 AND 5),

                         CONSTRAINT fk_review_profile
                             FOREIGN KEY (profile_id)
                                 REFERENCES profile(profileid)
                                 ON DELETE CASCADE,

                         CONSTRAINT fk_review_recipe
                             FOREIGN KEY (recipe_id)
                                 REFERENCES recipes(id)
                                 ON DELETE CASCADE
);

ALTER SEQUENCE reviews_id_seq
    OWNED BY reviews.review_id;