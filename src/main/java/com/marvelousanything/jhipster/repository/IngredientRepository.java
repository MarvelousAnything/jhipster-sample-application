package com.marvelousanything.jhipster.repository;

import com.marvelousanything.jhipster.domain.Ingredient;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Ingredient entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {}
