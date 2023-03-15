package com.marvelousanything.jhipster.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.marvelousanything.jhipster.domain.MeasuringUnit;
import com.marvelousanything.jhipster.repository.MeasuringUnitRepository;
import com.marvelousanything.jhipster.repository.search.MeasuringUnitSearchRepository;
import com.marvelousanything.jhipster.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.marvelousanything.jhipster.domain.MeasuringUnit}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class MeasuringUnitResource {

    private final Logger log = LoggerFactory.getLogger(MeasuringUnitResource.class);

    private static final String ENTITY_NAME = "measuringUnit";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MeasuringUnitRepository measuringUnitRepository;

    private final MeasuringUnitSearchRepository measuringUnitSearchRepository;

    public MeasuringUnitResource(
        MeasuringUnitRepository measuringUnitRepository,
        MeasuringUnitSearchRepository measuringUnitSearchRepository
    ) {
        this.measuringUnitRepository = measuringUnitRepository;
        this.measuringUnitSearchRepository = measuringUnitSearchRepository;
    }

    /**
     * {@code POST  /measuring-units} : Create a new measuringUnit.
     *
     * @param measuringUnit the measuringUnit to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new measuringUnit, or with status {@code 400 (Bad Request)} if the measuringUnit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/measuring-units")
    public ResponseEntity<MeasuringUnit> createMeasuringUnit(@Valid @RequestBody MeasuringUnit measuringUnit) throws URISyntaxException {
        log.debug("REST request to save MeasuringUnit : {}", measuringUnit);
        if (measuringUnit.getId() != null) {
            throw new BadRequestAlertException("A new measuringUnit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MeasuringUnit result = measuringUnitRepository.save(measuringUnit);
        measuringUnitSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/measuring-units/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /measuring-units/:id} : Updates an existing measuringUnit.
     *
     * @param id the id of the measuringUnit to save.
     * @param measuringUnit the measuringUnit to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated measuringUnit,
     * or with status {@code 400 (Bad Request)} if the measuringUnit is not valid,
     * or with status {@code 500 (Internal Server Error)} if the measuringUnit couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/measuring-units/{id}")
    public ResponseEntity<MeasuringUnit> updateMeasuringUnit(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MeasuringUnit measuringUnit
    ) throws URISyntaxException {
        log.debug("REST request to update MeasuringUnit : {}, {}", id, measuringUnit);
        if (measuringUnit.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, measuringUnit.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!measuringUnitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        MeasuringUnit result = measuringUnitRepository.save(measuringUnit);
        measuringUnitSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, measuringUnit.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /measuring-units/:id} : Partial updates given fields of an existing measuringUnit, field will ignore if it is null
     *
     * @param id the id of the measuringUnit to save.
     * @param measuringUnit the measuringUnit to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated measuringUnit,
     * or with status {@code 400 (Bad Request)} if the measuringUnit is not valid,
     * or with status {@code 404 (Not Found)} if the measuringUnit is not found,
     * or with status {@code 500 (Internal Server Error)} if the measuringUnit couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/measuring-units/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MeasuringUnit> partialUpdateMeasuringUnit(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MeasuringUnit measuringUnit
    ) throws URISyntaxException {
        log.debug("REST request to partial update MeasuringUnit partially : {}, {}", id, measuringUnit);
        if (measuringUnit.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, measuringUnit.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!measuringUnitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MeasuringUnit> result = measuringUnitRepository
            .findById(measuringUnit.getId())
            .map(existingMeasuringUnit -> {
                if (measuringUnit.getName() != null) {
                    existingMeasuringUnit.setName(measuringUnit.getName());
                }
                if (measuringUnit.getAbbreviation() != null) {
                    existingMeasuringUnit.setAbbreviation(measuringUnit.getAbbreviation());
                }

                return existingMeasuringUnit;
            })
            .map(measuringUnitRepository::save)
            .map(savedMeasuringUnit -> {
                measuringUnitSearchRepository.save(savedMeasuringUnit);

                return savedMeasuringUnit;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, measuringUnit.getId().toString())
        );
    }

    /**
     * {@code GET  /measuring-units} : get all the measuringUnits.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of measuringUnits in body.
     */
    @GetMapping("/measuring-units")
    public List<MeasuringUnit> getAllMeasuringUnits() {
        log.debug("REST request to get all MeasuringUnits");
        return measuringUnitRepository.findAll();
    }

    /**
     * {@code GET  /measuring-units/:id} : get the "id" measuringUnit.
     *
     * @param id the id of the measuringUnit to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the measuringUnit, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/measuring-units/{id}")
    public ResponseEntity<MeasuringUnit> getMeasuringUnit(@PathVariable Long id) {
        log.debug("REST request to get MeasuringUnit : {}", id);
        Optional<MeasuringUnit> measuringUnit = measuringUnitRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(measuringUnit);
    }

    /**
     * {@code DELETE  /measuring-units/:id} : delete the "id" measuringUnit.
     *
     * @param id the id of the measuringUnit to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/measuring-units/{id}")
    public ResponseEntity<Void> deleteMeasuringUnit(@PathVariable Long id) {
        log.debug("REST request to delete MeasuringUnit : {}", id);
        measuringUnitRepository.deleteById(id);
        measuringUnitSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/measuring-units?query=:query} : search for the measuringUnit corresponding
     * to the query.
     *
     * @param query the query of the measuringUnit search.
     * @return the result of the search.
     */
    @GetMapping("/_search/measuring-units")
    public List<MeasuringUnit> searchMeasuringUnits(@RequestParam String query) {
        log.debug("REST request to search MeasuringUnits for query {}", query);
        return StreamSupport.stream(measuringUnitSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
