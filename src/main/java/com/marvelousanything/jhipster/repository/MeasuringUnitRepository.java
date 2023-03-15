package com.marvelousanything.jhipster.repository;

import com.marvelousanything.jhipster.domain.MeasuringUnit;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MeasuringUnit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MeasuringUnitRepository extends JpaRepository<MeasuringUnit, Long> {}
