package com.marvelousanything.jhipster.repository;

import com.marvelousanything.jhipster.domain.RecipeIngredient;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RecipeIngredient entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {}
