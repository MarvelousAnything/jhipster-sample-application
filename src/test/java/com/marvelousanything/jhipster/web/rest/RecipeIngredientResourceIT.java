package com.marvelousanything.jhipster.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.marvelousanything.jhipster.IntegrationTest;
import com.marvelousanything.jhipster.domain.RecipeIngredient;
import com.marvelousanything.jhipster.repository.RecipeIngredientRepository;
import com.marvelousanything.jhipster.repository.search.RecipeIngredientSearchRepository;
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
 * Integration tests for the {@link RecipeIngredientResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RecipeIngredientResourceIT {

    private static final Integer DEFAULT_AMOUNT = 1;
    private static final Integer UPDATED_AMOUNT = 2;

    private static final String ENTITY_API_URL = "/api/recipe-ingredients";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/recipe-ingredients";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;

    @Autowired
    private RecipeIngredientSearchRepository recipeIngredientSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRecipeIngredientMockMvc;

    private RecipeIngredient recipeIngredient;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RecipeIngredient createEntity(EntityManager em) {
        RecipeIngredient recipeIngredient = new RecipeIngredient().amount(DEFAULT_AMOUNT);
        return recipeIngredient;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RecipeIngredient createUpdatedEntity(EntityManager em) {
        RecipeIngredient recipeIngredient = new RecipeIngredient().amount(UPDATED_AMOUNT);
        return recipeIngredient;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        recipeIngredientSearchRepository.deleteAll();
        assertThat(recipeIngredientSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        recipeIngredient = createEntity(em);
    }

    @Test
    @Transactional
    void createRecipeIngredient() throws Exception {
        int databaseSizeBeforeCreate = recipeIngredientRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeIngredientSearchRepository.findAll());
        // Create the RecipeIngredient
        restRecipeIngredientMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipeIngredient))
            )
            .andExpect(status().isCreated());

        // Validate the RecipeIngredient in the database
        List<RecipeIngredient> recipeIngredientList = recipeIngredientRepository.findAll();
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeIngredientSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        RecipeIngredient testRecipeIngredient = recipeIngredientList.get(recipeIngredientList.size() - 1);
        assertThat(testRecipeIngredient.getAmount()).isEqualTo(DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void createRecipeIngredientWithExistingId() throws Exception {
        // Create the RecipeIngredient with an existing ID
        recipeIngredient.setId(1L);

        int databaseSizeBeforeCreate = recipeIngredientRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeIngredientSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecipeIngredientMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipeIngredient))
            )
            .andExpect(status().isBadRequest());

        // Validate the RecipeIngredient in the database
        List<RecipeIngredient> recipeIngredientList = recipeIngredientRepository.findAll();
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeIngredientSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllRecipeIngredients() throws Exception {
        // Initialize the database
        recipeIngredientRepository.saveAndFlush(recipeIngredient);

        // Get all the recipeIngredientList
        restRecipeIngredientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recipeIngredient.getId().intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT)));
    }

    @Test
    @Transactional
    void getRecipeIngredient() throws Exception {
        // Initialize the database
        recipeIngredientRepository.saveAndFlush(recipeIngredient);

        // Get the recipeIngredient
        restRecipeIngredientMockMvc
            .perform(get(ENTITY_API_URL_ID, recipeIngredient.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(recipeIngredient.getId().intValue()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT));
    }

    @Test
    @Transactional
    void getNonExistingRecipeIngredient() throws Exception {
        // Get the recipeIngredient
        restRecipeIngredientMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRecipeIngredient() throws Exception {
        // Initialize the database
        recipeIngredientRepository.saveAndFlush(recipeIngredient);

        int databaseSizeBeforeUpdate = recipeIngredientRepository.findAll().size();
        recipeIngredientSearchRepository.save(recipeIngredient);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeIngredientSearchRepository.findAll());

        // Update the recipeIngredient
        RecipeIngredient updatedRecipeIngredient = recipeIngredientRepository.findById(recipeIngredient.getId()).get();
        // Disconnect from session so that the updates on updatedRecipeIngredient are not directly saved in db
        em.detach(updatedRecipeIngredient);
        updatedRecipeIngredient.amount(UPDATED_AMOUNT);

        restRecipeIngredientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRecipeIngredient.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedRecipeIngredient))
            )
            .andExpect(status().isOk());

        // Validate the RecipeIngredient in the database
        List<RecipeIngredient> recipeIngredientList = recipeIngredientRepository.findAll();
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeUpdate);
        RecipeIngredient testRecipeIngredient = recipeIngredientList.get(recipeIngredientList.size() - 1);
        assertThat(testRecipeIngredient.getAmount()).isEqualTo(UPDATED_AMOUNT);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeIngredientSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<RecipeIngredient> recipeIngredientSearchList = IterableUtils.toList(recipeIngredientSearchRepository.findAll());
                RecipeIngredient testRecipeIngredientSearch = recipeIngredientSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testRecipeIngredientSearch.getAmount()).isEqualTo(UPDATED_AMOUNT);
            });
    }

    @Test
    @Transactional
    void putNonExistingRecipeIngredient() throws Exception {
        int databaseSizeBeforeUpdate = recipeIngredientRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeIngredientSearchRepository.findAll());
        recipeIngredient.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecipeIngredientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, recipeIngredient.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(recipeIngredient))
            )
            .andExpect(status().isBadRequest());

        // Validate the RecipeIngredient in the database
        List<RecipeIngredient> recipeIngredientList = recipeIngredientRepository.findAll();
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeIngredientSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchRecipeIngredient() throws Exception {
        int databaseSizeBeforeUpdate = recipeIngredientRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeIngredientSearchRepository.findAll());
        recipeIngredient.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeIngredientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(recipeIngredient))
            )
            .andExpect(status().isBadRequest());

        // Validate the RecipeIngredient in the database
        List<RecipeIngredient> recipeIngredientList = recipeIngredientRepository.findAll();
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeIngredientSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRecipeIngredient() throws Exception {
        int databaseSizeBeforeUpdate = recipeIngredientRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeIngredientSearchRepository.findAll());
        recipeIngredient.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeIngredientMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipeIngredient))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RecipeIngredient in the database
        List<RecipeIngredient> recipeIngredientList = recipeIngredientRepository.findAll();
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeIngredientSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateRecipeIngredientWithPatch() throws Exception {
        // Initialize the database
        recipeIngredientRepository.saveAndFlush(recipeIngredient);

        int databaseSizeBeforeUpdate = recipeIngredientRepository.findAll().size();

        // Update the recipeIngredient using partial update
        RecipeIngredient partialUpdatedRecipeIngredient = new RecipeIngredient();
        partialUpdatedRecipeIngredient.setId(recipeIngredient.getId());

        partialUpdatedRecipeIngredient.amount(UPDATED_AMOUNT);

        restRecipeIngredientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRecipeIngredient.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRecipeIngredient))
            )
            .andExpect(status().isOk());

        // Validate the RecipeIngredient in the database
        List<RecipeIngredient> recipeIngredientList = recipeIngredientRepository.findAll();
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeUpdate);
        RecipeIngredient testRecipeIngredient = recipeIngredientList.get(recipeIngredientList.size() - 1);
        assertThat(testRecipeIngredient.getAmount()).isEqualTo(UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void fullUpdateRecipeIngredientWithPatch() throws Exception {
        // Initialize the database
        recipeIngredientRepository.saveAndFlush(recipeIngredient);

        int databaseSizeBeforeUpdate = recipeIngredientRepository.findAll().size();

        // Update the recipeIngredient using partial update
        RecipeIngredient partialUpdatedRecipeIngredient = new RecipeIngredient();
        partialUpdatedRecipeIngredient.setId(recipeIngredient.getId());

        partialUpdatedRecipeIngredient.amount(UPDATED_AMOUNT);

        restRecipeIngredientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRecipeIngredient.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRecipeIngredient))
            )
            .andExpect(status().isOk());

        // Validate the RecipeIngredient in the database
        List<RecipeIngredient> recipeIngredientList = recipeIngredientRepository.findAll();
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeUpdate);
        RecipeIngredient testRecipeIngredient = recipeIngredientList.get(recipeIngredientList.size() - 1);
        assertThat(testRecipeIngredient.getAmount()).isEqualTo(UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void patchNonExistingRecipeIngredient() throws Exception {
        int databaseSizeBeforeUpdate = recipeIngredientRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeIngredientSearchRepository.findAll());
        recipeIngredient.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecipeIngredientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, recipeIngredient.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(recipeIngredient))
            )
            .andExpect(status().isBadRequest());

        // Validate the RecipeIngredient in the database
        List<RecipeIngredient> recipeIngredientList = recipeIngredientRepository.findAll();
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeIngredientSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRecipeIngredient() throws Exception {
        int databaseSizeBeforeUpdate = recipeIngredientRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeIngredientSearchRepository.findAll());
        recipeIngredient.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeIngredientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(recipeIngredient))
            )
            .andExpect(status().isBadRequest());

        // Validate the RecipeIngredient in the database
        List<RecipeIngredient> recipeIngredientList = recipeIngredientRepository.findAll();
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeIngredientSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRecipeIngredient() throws Exception {
        int databaseSizeBeforeUpdate = recipeIngredientRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeIngredientSearchRepository.findAll());
        recipeIngredient.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeIngredientMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(recipeIngredient))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RecipeIngredient in the database
        List<RecipeIngredient> recipeIngredientList = recipeIngredientRepository.findAll();
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeIngredientSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteRecipeIngredient() throws Exception {
        // Initialize the database
        recipeIngredientRepository.saveAndFlush(recipeIngredient);
        recipeIngredientRepository.save(recipeIngredient);
        recipeIngredientSearchRepository.save(recipeIngredient);

        int databaseSizeBeforeDelete = recipeIngredientRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeIngredientSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the recipeIngredient
        restRecipeIngredientMockMvc
            .perform(delete(ENTITY_API_URL_ID, recipeIngredient.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RecipeIngredient> recipeIngredientList = recipeIngredientRepository.findAll();
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeIngredientSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchRecipeIngredient() throws Exception {
        // Initialize the database
        recipeIngredient = recipeIngredientRepository.saveAndFlush(recipeIngredient);
        recipeIngredientSearchRepository.save(recipeIngredient);

        // Search the recipeIngredient
        restRecipeIngredientMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + recipeIngredient.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recipeIngredient.getId().intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT)));
    }
}
