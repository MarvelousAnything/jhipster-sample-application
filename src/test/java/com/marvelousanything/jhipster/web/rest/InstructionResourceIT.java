package com.marvelousanything.jhipster.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.marvelousanything.jhipster.IntegrationTest;
import com.marvelousanything.jhipster.domain.Instruction;
import com.marvelousanything.jhipster.repository.InstructionRepository;
import com.marvelousanything.jhipster.repository.search.InstructionSearchRepository;
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
 * Integration tests for the {@link InstructionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InstructionResourceIT {

    private static final Integer DEFAULT_STEP_NUMBER = 1;
    private static final Integer UPDATED_STEP_NUMBER = 2;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/instructions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/instructions";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InstructionRepository instructionRepository;

    @Autowired
    private InstructionSearchRepository instructionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInstructionMockMvc;

    private Instruction instruction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Instruction createEntity(EntityManager em) {
        Instruction instruction = new Instruction().stepNumber(DEFAULT_STEP_NUMBER).description(DEFAULT_DESCRIPTION);
        return instruction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Instruction createUpdatedEntity(EntityManager em) {
        Instruction instruction = new Instruction().stepNumber(UPDATED_STEP_NUMBER).description(UPDATED_DESCRIPTION);
        return instruction;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        instructionSearchRepository.deleteAll();
        assertThat(instructionSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        instruction = createEntity(em);
    }

    @Test
    @Transactional
    void createInstruction() throws Exception {
        int databaseSizeBeforeCreate = instructionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instructionSearchRepository.findAll());
        // Create the Instruction
        restInstructionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(instruction)))
            .andExpect(status().isCreated());

        // Validate the Instruction in the database
        List<Instruction> instructionList = instructionRepository.findAll();
        assertThat(instructionList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(instructionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Instruction testInstruction = instructionList.get(instructionList.size() - 1);
        assertThat(testInstruction.getStepNumber()).isEqualTo(DEFAULT_STEP_NUMBER);
        assertThat(testInstruction.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createInstructionWithExistingId() throws Exception {
        // Create the Instruction with an existing ID
        instruction.setId(1L);

        int databaseSizeBeforeCreate = instructionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instructionSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restInstructionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(instruction)))
            .andExpect(status().isBadRequest());

        // Validate the Instruction in the database
        List<Instruction> instructionList = instructionRepository.findAll();
        assertThat(instructionList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(instructionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllInstructions() throws Exception {
        // Initialize the database
        instructionRepository.saveAndFlush(instruction);

        // Get all the instructionList
        restInstructionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(instruction.getId().intValue())))
            .andExpect(jsonPath("$.[*].stepNumber").value(hasItem(DEFAULT_STEP_NUMBER)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getInstruction() throws Exception {
        // Initialize the database
        instructionRepository.saveAndFlush(instruction);

        // Get the instruction
        restInstructionMockMvc
            .perform(get(ENTITY_API_URL_ID, instruction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(instruction.getId().intValue()))
            .andExpect(jsonPath("$.stepNumber").value(DEFAULT_STEP_NUMBER))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingInstruction() throws Exception {
        // Get the instruction
        restInstructionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInstruction() throws Exception {
        // Initialize the database
        instructionRepository.saveAndFlush(instruction);

        int databaseSizeBeforeUpdate = instructionRepository.findAll().size();
        instructionSearchRepository.save(instruction);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instructionSearchRepository.findAll());

        // Update the instruction
        Instruction updatedInstruction = instructionRepository.findById(instruction.getId()).get();
        // Disconnect from session so that the updates on updatedInstruction are not directly saved in db
        em.detach(updatedInstruction);
        updatedInstruction.stepNumber(UPDATED_STEP_NUMBER).description(UPDATED_DESCRIPTION);

        restInstructionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedInstruction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedInstruction))
            )
            .andExpect(status().isOk());

        // Validate the Instruction in the database
        List<Instruction> instructionList = instructionRepository.findAll();
        assertThat(instructionList).hasSize(databaseSizeBeforeUpdate);
        Instruction testInstruction = instructionList.get(instructionList.size() - 1);
        assertThat(testInstruction.getStepNumber()).isEqualTo(UPDATED_STEP_NUMBER);
        assertThat(testInstruction.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(instructionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Instruction> instructionSearchList = IterableUtils.toList(instructionSearchRepository.findAll());
                Instruction testInstructionSearch = instructionSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testInstructionSearch.getStepNumber()).isEqualTo(UPDATED_STEP_NUMBER);
                assertThat(testInstructionSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    @Transactional
    void putNonExistingInstruction() throws Exception {
        int databaseSizeBeforeUpdate = instructionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instructionSearchRepository.findAll());
        instruction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInstructionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, instruction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(instruction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Instruction in the database
        List<Instruction> instructionList = instructionRepository.findAll();
        assertThat(instructionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(instructionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchInstruction() throws Exception {
        int databaseSizeBeforeUpdate = instructionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instructionSearchRepository.findAll());
        instruction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstructionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(instruction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Instruction in the database
        List<Instruction> instructionList = instructionRepository.findAll();
        assertThat(instructionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(instructionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInstruction() throws Exception {
        int databaseSizeBeforeUpdate = instructionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instructionSearchRepository.findAll());
        instruction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstructionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(instruction)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Instruction in the database
        List<Instruction> instructionList = instructionRepository.findAll();
        assertThat(instructionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(instructionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateInstructionWithPatch() throws Exception {
        // Initialize the database
        instructionRepository.saveAndFlush(instruction);

        int databaseSizeBeforeUpdate = instructionRepository.findAll().size();

        // Update the instruction using partial update
        Instruction partialUpdatedInstruction = new Instruction();
        partialUpdatedInstruction.setId(instruction.getId());

        partialUpdatedInstruction.stepNumber(UPDATED_STEP_NUMBER).description(UPDATED_DESCRIPTION);

        restInstructionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInstruction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInstruction))
            )
            .andExpect(status().isOk());

        // Validate the Instruction in the database
        List<Instruction> instructionList = instructionRepository.findAll();
        assertThat(instructionList).hasSize(databaseSizeBeforeUpdate);
        Instruction testInstruction = instructionList.get(instructionList.size() - 1);
        assertThat(testInstruction.getStepNumber()).isEqualTo(UPDATED_STEP_NUMBER);
        assertThat(testInstruction.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateInstructionWithPatch() throws Exception {
        // Initialize the database
        instructionRepository.saveAndFlush(instruction);

        int databaseSizeBeforeUpdate = instructionRepository.findAll().size();

        // Update the instruction using partial update
        Instruction partialUpdatedInstruction = new Instruction();
        partialUpdatedInstruction.setId(instruction.getId());

        partialUpdatedInstruction.stepNumber(UPDATED_STEP_NUMBER).description(UPDATED_DESCRIPTION);

        restInstructionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInstruction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInstruction))
            )
            .andExpect(status().isOk());

        // Validate the Instruction in the database
        List<Instruction> instructionList = instructionRepository.findAll();
        assertThat(instructionList).hasSize(databaseSizeBeforeUpdate);
        Instruction testInstruction = instructionList.get(instructionList.size() - 1);
        assertThat(testInstruction.getStepNumber()).isEqualTo(UPDATED_STEP_NUMBER);
        assertThat(testInstruction.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingInstruction() throws Exception {
        int databaseSizeBeforeUpdate = instructionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instructionSearchRepository.findAll());
        instruction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInstructionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, instruction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(instruction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Instruction in the database
        List<Instruction> instructionList = instructionRepository.findAll();
        assertThat(instructionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(instructionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInstruction() throws Exception {
        int databaseSizeBeforeUpdate = instructionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instructionSearchRepository.findAll());
        instruction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstructionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(instruction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Instruction in the database
        List<Instruction> instructionList = instructionRepository.findAll();
        assertThat(instructionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(instructionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInstruction() throws Exception {
        int databaseSizeBeforeUpdate = instructionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instructionSearchRepository.findAll());
        instruction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstructionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(instruction))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Instruction in the database
        List<Instruction> instructionList = instructionRepository.findAll();
        assertThat(instructionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(instructionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteInstruction() throws Exception {
        // Initialize the database
        instructionRepository.saveAndFlush(instruction);
        instructionRepository.save(instruction);
        instructionSearchRepository.save(instruction);

        int databaseSizeBeforeDelete = instructionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(instructionSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the instruction
        restInstructionMockMvc
            .perform(delete(ENTITY_API_URL_ID, instruction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Instruction> instructionList = instructionRepository.findAll();
        assertThat(instructionList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(instructionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchInstruction() throws Exception {
        // Initialize the database
        instruction = instructionRepository.saveAndFlush(instruction);
        instructionSearchRepository.save(instruction);

        // Search the instruction
        restInstructionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + instruction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(instruction.getId().intValue())))
            .andExpect(jsonPath("$.[*].stepNumber").value(hasItem(DEFAULT_STEP_NUMBER)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}
