package com.marvelousanything.jhipster.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.marvelousanything.jhipster.IntegrationTest;
import com.marvelousanything.jhipster.domain.Recipe;
import com.marvelousanything.jhipster.repository.RecipeRepository;
import com.marvelousanything.jhipster.repository.search.RecipeSearchRepository;
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
 * Integration tests for the {@link RecipeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RecipeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_PREP_TIME = 1;
    private static final Integer UPDATED_PREP_TIME = 2;

    private static final Integer DEFAULT_COOK_TIME = 1;
    private static final Integer UPDATED_COOK_TIME = 2;

    private static final Integer DEFAULT_SERVINGS = 1;
    private static final Integer UPDATED_SERVINGS = 2;

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/recipes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/recipes";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RecipeSearchRepository recipeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRecipeMockMvc;

    private Recipe recipe;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Recipe createEntity(EntityManager em) {
        Recipe recipe = new Recipe()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .prepTime(DEFAULT_PREP_TIME)
            .cookTime(DEFAULT_COOK_TIME)
            .servings(DEFAULT_SERVINGS)
            .imageUrl(DEFAULT_IMAGE_URL);
        return recipe;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Recipe createUpdatedEntity(EntityManager em) {
        Recipe recipe = new Recipe()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .prepTime(UPDATED_PREP_TIME)
            .cookTime(UPDATED_COOK_TIME)
            .servings(UPDATED_SERVINGS)
            .imageUrl(UPDATED_IMAGE_URL);
        return recipe;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        recipeSearchRepository.deleteAll();
        assertThat(recipeSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        recipe = createEntity(em);
    }

    @Test
    @Transactional
    void createRecipe() throws Exception {
        int databaseSizeBeforeCreate = recipeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        // Create the Recipe
        restRecipeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipe)))
            .andExpect(status().isCreated());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Recipe testRecipe = recipeList.get(recipeList.size() - 1);
        assertThat(testRecipe.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRecipe.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRecipe.getPrepTime()).isEqualTo(DEFAULT_PREP_TIME);
        assertThat(testRecipe.getCookTime()).isEqualTo(DEFAULT_COOK_TIME);
        assertThat(testRecipe.getServings()).isEqualTo(DEFAULT_SERVINGS);
        assertThat(testRecipe.getImageUrl()).isEqualTo(DEFAULT_IMAGE_URL);
    }

    @Test
    @Transactional
    void createRecipeWithExistingId() throws Exception {
        // Create the Recipe with an existing ID
        recipe.setId(1L);

        int databaseSizeBeforeCreate = recipeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecipeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipe)))
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = recipeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        // set the field null
        recipe.setName(null);

        // Create the Recipe, which fails.

        restRecipeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipe)))
            .andExpect(status().isBadRequest());

        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllRecipes() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList
        restRecipeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recipe.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].prepTime").value(hasItem(DEFAULT_PREP_TIME)))
            .andExpect(jsonPath("$.[*].cookTime").value(hasItem(DEFAULT_COOK_TIME)))
            .andExpect(jsonPath("$.[*].servings").value(hasItem(DEFAULT_SERVINGS)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)));
    }

    @Test
    @Transactional
    void getRecipe() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get the recipe
        restRecipeMockMvc
            .perform(get(ENTITY_API_URL_ID, recipe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(recipe.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.prepTime").value(DEFAULT_PREP_TIME))
            .andExpect(jsonPath("$.cookTime").value(DEFAULT_COOK_TIME))
            .andExpect(jsonPath("$.servings").value(DEFAULT_SERVINGS))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL));
    }

    @Test
    @Transactional
    void getNonExistingRecipe() throws Exception {
        // Get the recipe
        restRecipeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRecipe() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        recipeSearchRepository.save(recipe);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeSearchRepository.findAll());

        // Update the recipe
        Recipe updatedRecipe = recipeRepository.findById(recipe.getId()).get();
        // Disconnect from session so that the updates on updatedRecipe are not directly saved in db
        em.detach(updatedRecipe);
        updatedRecipe
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .prepTime(UPDATED_PREP_TIME)
            .cookTime(UPDATED_COOK_TIME)
            .servings(UPDATED_SERVINGS)
            .imageUrl(UPDATED_IMAGE_URL);

        restRecipeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRecipe.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedRecipe))
            )
            .andExpect(status().isOk());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        Recipe testRecipe = recipeList.get(recipeList.size() - 1);
        assertThat(testRecipe.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRecipe.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRecipe.getPrepTime()).isEqualTo(UPDATED_PREP_TIME);
        assertThat(testRecipe.getCookTime()).isEqualTo(UPDATED_COOK_TIME);
        assertThat(testRecipe.getServings()).isEqualTo(UPDATED_SERVINGS);
        assertThat(testRecipe.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Recipe> recipeSearchList = IterableUtils.toList(recipeSearchRepository.findAll());
                Recipe testRecipeSearch = recipeSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testRecipeSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testRecipeSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testRecipeSearch.getPrepTime()).isEqualTo(UPDATED_PREP_TIME);
                assertThat(testRecipeSearch.getCookTime()).isEqualTo(UPDATED_COOK_TIME);
                assertThat(testRecipeSearch.getServings()).isEqualTo(UPDATED_SERVINGS);
                assertThat(testRecipeSearch.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
            });
    }

    @Test
    @Transactional
    void putNonExistingRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        recipe.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecipeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, recipe.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(recipe))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        recipe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(recipe))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        recipe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipe)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateRecipeWithPatch() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();

        // Update the recipe using partial update
        Recipe partialUpdatedRecipe = new Recipe();
        partialUpdatedRecipe.setId(recipe.getId());

        partialUpdatedRecipe.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restRecipeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRecipe.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRecipe))
            )
            .andExpect(status().isOk());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        Recipe testRecipe = recipeList.get(recipeList.size() - 1);
        assertThat(testRecipe.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRecipe.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRecipe.getPrepTime()).isEqualTo(DEFAULT_PREP_TIME);
        assertThat(testRecipe.getCookTime()).isEqualTo(DEFAULT_COOK_TIME);
        assertThat(testRecipe.getServings()).isEqualTo(DEFAULT_SERVINGS);
        assertThat(testRecipe.getImageUrl()).isEqualTo(DEFAULT_IMAGE_URL);
    }

    @Test
    @Transactional
    void fullUpdateRecipeWithPatch() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();

        // Update the recipe using partial update
        Recipe partialUpdatedRecipe = new Recipe();
        partialUpdatedRecipe.setId(recipe.getId());

        partialUpdatedRecipe
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .prepTime(UPDATED_PREP_TIME)
            .cookTime(UPDATED_COOK_TIME)
            .servings(UPDATED_SERVINGS)
            .imageUrl(UPDATED_IMAGE_URL);

        restRecipeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRecipe.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRecipe))
            )
            .andExpect(status().isOk());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        Recipe testRecipe = recipeList.get(recipeList.size() - 1);
        assertThat(testRecipe.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRecipe.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRecipe.getPrepTime()).isEqualTo(UPDATED_PREP_TIME);
        assertThat(testRecipe.getCookTime()).isEqualTo(UPDATED_COOK_TIME);
        assertThat(testRecipe.getServings()).isEqualTo(UPDATED_SERVINGS);
        assertThat(testRecipe.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
    }

    @Test
    @Transactional
    void patchNonExistingRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        recipe.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecipeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, recipe.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(recipe))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        recipe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(recipe))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        recipe.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(recipe)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteRecipe() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);
        recipeRepository.save(recipe);
        recipeSearchRepository.save(recipe);

        int databaseSizeBeforeDelete = recipeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the recipe
        restRecipeMockMvc
            .perform(delete(ENTITY_API_URL_ID, recipe.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(recipeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchRecipe() throws Exception {
        // Initialize the database
        recipe = recipeRepository.saveAndFlush(recipe);
        recipeSearchRepository.save(recipe);

        // Search the recipe
        restRecipeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + recipe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recipe.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].prepTime").value(hasItem(DEFAULT_PREP_TIME)))
            .andExpect(jsonPath("$.[*].cookTime").value(hasItem(DEFAULT_COOK_TIME)))
            .andExpect(jsonPath("$.[*].servings").value(hasItem(DEFAULT_SERVINGS)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL)));
    }
}
