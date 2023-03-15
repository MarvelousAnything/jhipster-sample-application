package com.marvelousanything.jhipster.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.marvelousanything.jhipster.IntegrationTest;
import com.marvelousanything.jhipster.domain.MeasuringUnit;
import com.marvelousanything.jhipster.repository.MeasuringUnitRepository;
import com.marvelousanything.jhipster.repository.search.MeasuringUnitSearchRepository;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link MeasuringUnitResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MeasuringUnitResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ABBREVIATION = "AAAAAAAAAA";
    private static final String UPDATED_ABBREVIATION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/measuring-units";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/measuring-units";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MeasuringUnitRepository measuringUnitRepository;

    @Autowired
    private MeasuringUnitSearchRepository measuringUnitSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMeasuringUnitMockMvc;

    private MeasuringUnit measuringUnit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MeasuringUnit createEntity(EntityManager em) {
        MeasuringUnit measuringUnit = new MeasuringUnit().name(DEFAULT_NAME).abbreviation(DEFAULT_ABBREVIATION);
        return measuringUnit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MeasuringUnit createUpdatedEntity(EntityManager em) {
        MeasuringUnit measuringUnit = new MeasuringUnit().name(UPDATED_NAME).abbreviation(UPDATED_ABBREVIATION);
        return measuringUnit;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        measuringUnitSearchRepository.deleteAll();
        assertThat(measuringUnitSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        measuringUnit = createEntity(em);
    }

    @Test
    @Transactional
    void createMeasuringUnit() throws Exception {
        int databaseSizeBeforeCreate = measuringUnitRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(measuringUnitSearchRepository.findAll());
        // Create the MeasuringUnit
        restMeasuringUnitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(measuringUnit)))
            .andExpect(status().isCreated());

        // Validate the MeasuringUnit in the database
        List<MeasuringUnit> measuringUnitList = measuringUnitRepository.findAll();
        assertThat(measuringUnitList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(measuringUnitSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        MeasuringUnit testMeasuringUnit = measuringUnitList.get(measuringUnitList.size() - 1);
        assertThat(testMeasuringUnit.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMeasuringUnit.getAbbreviation()).isEqualTo(DEFAULT_ABBREVIATION);
    }

    @Test
    @Transactional
    void createMeasuringUnitWithExistingId() throws Exception {
        // Create the MeasuringUnit with an existing ID
        measuringUnit.setId(1L);

        int databaseSizeBeforeCreate = measuringUnitRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(measuringUnitSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restMeasuringUnitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(measuringUnit)))
            .andExpect(status().isBadRequest());

        // Validate the MeasuringUnit in the database
        List<MeasuringUnit> measuringUnitList = measuringUnitRepository.findAll();
        assertThat(measuringUnitList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(measuringUnitSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = measuringUnitRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(measuringUnitSearchRepository.findAll());
        // set the field null
        measuringUnit.setName(null);

        // Create the MeasuringUnit, which fails.

        restMeasuringUnitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(measuringUnit)))
            .andExpect(status().isBadRequest());

        List<MeasuringUnit> measuringUnitList = measuringUnitRepository.findAll();
        assertThat(measuringUnitList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(measuringUnitSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllMeasuringUnits() throws Exception {
        // Initialize the database
        measuringUnitRepository.saveAndFlush(measuringUnit);

        // Get all the measuringUnitList
        restMeasuringUnitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(measuringUnit.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].abbreviation").value(hasItem(DEFAULT_ABBREVIATION)));
    }

    @Test
    @Transactional
    void getMeasuringUnit() throws Exception {
        // Initialize the database
        measuringUnitRepository.saveAndFlush(measuringUnit);

        // Get the measuringUnit
        restMeasuringUnitMockMvc
            .perform(get(ENTITY_API_URL_ID, measuringUnit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(measuringUnit.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.abbreviation").value(DEFAULT_ABBREVIATION));
    }

    @Test
    @Transactional
    void getNonExistingMeasuringUnit() throws Exception {
        // Get the measuringUnit
        restMeasuringUnitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMeasuringUnit() throws Exception {
        // Initialize the database
        measuringUnitRepository.saveAndFlush(measuringUnit);

        int databaseSizeBeforeUpdate = measuringUnitRepository.findAll().size();
        measuringUnitSearchRepository.save(measuringUnit);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(measuringUnitSearchRepository.findAll());

        // Update the measuringUnit
        MeasuringUnit updatedMeasuringUnit = measuringUnitRepository.findById(measuringUnit.getId()).get();
        // Disconnect from session so that the updates on updatedMeasuringUnit are not directly saved in db
        em.detach(updatedMeasuringUnit);
        updatedMeasuringUnit.name(UPDATED_NAME).abbreviation(UPDATED_ABBREVIATION);

        restMeasuringUnitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMeasuringUnit.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedMeasuringUnit))
            )
            .andExpect(status().isOk());

        // Validate the MeasuringUnit in the database
        List<MeasuringUnit> measuringUnitList = measuringUnitRepository.findAll();
        assertThat(measuringUnitList).hasSize(databaseSizeBeforeUpdate);
        MeasuringUnit testMeasuringUnit = measuringUnitList.get(measuringUnitList.size() - 1);
        assertThat(testMeasuringUnit.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMeasuringUnit.getAbbreviation()).isEqualTo(UPDATED_ABBREVIATION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(measuringUnitSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MeasuringUnit> measuringUnitSearchList = IterableUtils.toList(measuringUnitSearchRepository.findAll());
                MeasuringUnit testMeasuringUnitSearch = measuringUnitSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testMeasuringUnitSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testMeasuringUnitSearch.getAbbreviation()).isEqualTo(UPDATED_ABBREVIATION);
            });
    }

    @Test
    @Transactional
    void putNonExistingMeasuringUnit() throws Exception {
        int databaseSizeBeforeUpdate = measuringUnitRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(measuringUnitSearchRepository.findAll());
        measuringUnit.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMeasuringUnitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, measuringUnit.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(measuringUnit))
            )
            .andExpect(status().isBadRequest());

        // Validate the MeasuringUnit in the database
        List<MeasuringUnit> measuringUnitList = measuringUnitRepository.findAll();
        assertThat(measuringUnitList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(measuringUnitSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchMeasuringUnit() throws Exception {
        int databaseSizeBeforeUpdate = measuringUnitRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(measuringUnitSearchRepository.findAll());
        measuringUnit.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMeasuringUnitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(measuringUnit))
            )
            .andExpect(status().isBadRequest());

        // Validate the MeasuringUnit in the database
        List<MeasuringUnit> measuringUnitList = measuringUnitRepository.findAll();
        assertThat(measuringUnitList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(measuringUnitSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMeasuringUnit() throws Exception {
        int databaseSizeBeforeUpdate = measuringUnitRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(measuringUnitSearchRepository.findAll());
        measuringUnit.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMeasuringUnitMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(measuringUnit)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MeasuringUnit in the database
        List<MeasuringUnit> measuringUnitList = measuringUnitRepository.findAll();
        assertThat(measuringUnitList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(measuringUnitSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateMeasuringUnitWithPatch() throws Exception {
        // Initialize the database
        measuringUnitRepository.saveAndFlush(measuringUnit);

        int databaseSizeBeforeUpdate = measuringUnitRepository.findAll().size();

        // Update the measuringUnit using partial update
        MeasuringUnit partialUpdatedMeasuringUnit = new MeasuringUnit();
        partialUpdatedMeasuringUnit.setId(measuringUnit.getId());

        partialUpdatedMeasuringUnit.name(UPDATED_NAME).abbreviation(UPDATED_ABBREVIATION);

        restMeasuringUnitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMeasuringUnit.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMeasuringUnit))
            )
            .andExpect(status().isOk());

        // Validate the MeasuringUnit in the database
        List<MeasuringUnit> measuringUnitList = measuringUnitRepository.findAll();
        assertThat(measuringUnitList).hasSize(databaseSizeBeforeUpdate);
        MeasuringUnit testMeasuringUnit = measuringUnitList.get(measuringUnitList.size() - 1);
        assertThat(testMeasuringUnit.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMeasuringUnit.getAbbreviation()).isEqualTo(UPDATED_ABBREVIATION);
    }

    @Test
    @Transactional
    void fullUpdateMeasuringUnitWithPatch() throws Exception {
        // Initialize the database
        measuringUnitRepository.saveAndFlush(measuringUnit);

        int databaseSizeBeforeUpdate = measuringUnitRepository.findAll().size();

        // Update the measuringUnit using partial update
        MeasuringUnit partialUpdatedMeasuringUnit = new MeasuringUnit();
        partialUpdatedMeasuringUnit.setId(measuringUnit.getId());

        partialUpdatedMeasuringUnit.name(UPDATED_NAME).abbreviation(UPDATED_ABBREVIATION);

        restMeasuringUnitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMeasuringUnit.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMeasuringUnit))
            )
            .andExpect(status().isOk());

        // Validate the MeasuringUnit in the database
        List<MeasuringUnit> measuringUnitList = measuringUnitRepository.findAll();
        assertThat(measuringUnitList).hasSize(databaseSizeBeforeUpdate);
        MeasuringUnit testMeasuringUnit = measuringUnitList.get(measuringUnitList.size() - 1);
        assertThat(testMeasuringUnit.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMeasuringUnit.getAbbreviation()).isEqualTo(UPDATED_ABBREVIATION);
    }

    @Test
    @Transactional
    void patchNonExistingMeasuringUnit() throws Exception {
        int databaseSizeBeforeUpdate = measuringUnitRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(measuringUnitSearchRepository.findAll());
        measuringUnit.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMeasuringUnitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, measuringUnit.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(measuringUnit))
            )
            .andExpect(status().isBadRequest());

        // Validate the MeasuringUnit in the database
        List<MeasuringUnit> measuringUnitList = measuringUnitRepository.findAll();
        assertThat(measuringUnitList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(measuringUnitSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMeasuringUnit() throws Exception {
        int databaseSizeBeforeUpdate = measuringUnitRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(measuringUnitSearchRepository.findAll());
        measuringUnit.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMeasuringUnitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(measuringUnit))
            )
            .andExpect(status().isBadRequest());

        // Validate the MeasuringUnit in the database
        List<MeasuringUnit> measuringUnitList = measuringUnitRepository.findAll();
        assertThat(measuringUnitList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(measuringUnitSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMeasuringUnit() throws Exception {
        int databaseSizeBeforeUpdate = measuringUnitRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(measuringUnitSearchRepository.findAll());
        measuringUnit.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMeasuringUnitMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(measuringUnit))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the MeasuringUnit in the database
        List<MeasuringUnit> measuringUnitList = measuringUnitRepository.findAll();
        assertThat(measuringUnitList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(measuringUnitSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteMeasuringUnit() throws Exception {
        // Initialize the database
        measuringUnitRepository.saveAndFlush(measuringUnit);
        measuringUnitRepository.save(measuringUnit);
        measuringUnitSearchRepository.save(measuringUnit);

        int databaseSizeBeforeDelete = measuringUnitRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(measuringUnitSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the measuringUnit
        restMeasuringUnitMockMvc
            .perform(delete(ENTITY_API_URL_ID, measuringUnit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<MeasuringUnit> measuringUnitList = measuringUnitRepository.findAll();
        assertThat(measuringUnitList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(measuringUnitSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchMeasuringUnit() throws Exception {
        // Initialize the database
        measuringUnit = measuringUnitRepository.saveAndFlush(measuringUnit);
        measuringUnitSearchRepository.save(measuringUnit);

        // Search the measuringUnit
        restMeasuringUnitMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + measuringUnit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(measuringUnit.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].abbreviation").value(hasItem(DEFAULT_ABBREVIATION)));
    }
}
