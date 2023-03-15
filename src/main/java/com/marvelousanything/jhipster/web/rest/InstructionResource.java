package com.marvelousanything.jhipster.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.marvelousanything.jhipster.domain.Instruction;
import com.marvelousanything.jhipster.repository.InstructionRepository;
import com.marvelousanything.jhipster.repository.search.InstructionSearchRepository;
import com.marvelousanything.jhipster.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.marvelousanything.jhipster.domain.Instruction}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class InstructionResource {

    private final Logger log = LoggerFactory.getLogger(InstructionResource.class);

    private static final String ENTITY_NAME = "instruction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InstructionRepository instructionRepository;

    private final InstructionSearchRepository instructionSearchRepository;

    public InstructionResource(InstructionRepository instructionRepository, InstructionSearchRepository instructionSearchRepository) {
        this.instructionRepository = instructionRepository;
        this.instructionSearchRepository = instructionSearchRepository;
    }

    /**
     * {@code POST  /instructions} : Create a new instruction.
     *
     * @param instruction the instruction to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new instruction, or with status {@code 400 (Bad Request)} if the instruction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/instructions")
    public ResponseEntity<Instruction> createInstruction(@RequestBody Instruction instruction) throws URISyntaxException {
        log.debug("REST request to save Instruction : {}", instruction);
        if (instruction.getId() != null) {
            throw new BadRequestAlertException("A new instruction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Instruction result = instructionRepository.save(instruction);
        instructionSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/instructions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /instructions/:id} : Updates an existing instruction.
     *
     * @param id the id of the instruction to save.
     * @param instruction the instruction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated instruction,
     * or with status {@code 400 (Bad Request)} if the instruction is not valid,
     * or with status {@code 500 (Internal Server Error)} if the instruction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/instructions/{id}")
    public ResponseEntity<Instruction> updateInstruction(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Instruction instruction
    ) throws URISyntaxException {
        log.debug("REST request to update Instruction : {}, {}", id, instruction);
        if (instruction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, instruction.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!instructionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Instruction result = instructionRepository.save(instruction);
        instructionSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, instruction.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /instructions/:id} : Partial updates given fields of an existing instruction, field will ignore if it is null
     *
     * @param id the id of the instruction to save.
     * @param instruction the instruction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated instruction,
     * or with status {@code 400 (Bad Request)} if the instruction is not valid,
     * or with status {@code 404 (Not Found)} if the instruction is not found,
     * or with status {@code 500 (Internal Server Error)} if the instruction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/instructions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Instruction> partialUpdateInstruction(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Instruction instruction
    ) throws URISyntaxException {
        log.debug("REST request to partial update Instruction partially : {}, {}", id, instruction);
        if (instruction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, instruction.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!instructionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Instruction> result = instructionRepository
            .findById(instruction.getId())
            .map(existingInstruction -> {
                if (instruction.getStepNumber() != null) {
                    existingInstruction.setStepNumber(instruction.getStepNumber());
                }
                if (instruction.getDescription() != null) {
                    existingInstruction.setDescription(instruction.getDescription());
                }

                return existingInstruction;
            })
            .map(instructionRepository::save)
            .map(savedInstruction -> {
                instructionSearchRepository.save(savedInstruction);

                return savedInstruction;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, instruction.getId().toString())
        );
    }

    /**
     * {@code GET  /instructions} : get all the instructions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of instructions in body.
     */
    @GetMapping("/instructions")
    public List<Instruction> getAllInstructions() {
        log.debug("REST request to get all Instructions");
        return instructionRepository.findAll();
    }

    /**
     * {@code GET  /instructions/:id} : get the "id" instruction.
     *
     * @param id the id of the instruction to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the instruction, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/instructions/{id}")
    public ResponseEntity<Instruction> getInstruction(@PathVariable Long id) {
        log.debug("REST request to get Instruction : {}", id);
        Optional<Instruction> instruction = instructionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(instruction);
    }

    /**
     * {@code DELETE  /instructions/:id} : delete the "id" instruction.
     *
     * @param id the id of the instruction to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/instructions/{id}")
    public ResponseEntity<Void> deleteInstruction(@PathVariable Long id) {
        log.debug("REST request to delete Instruction : {}", id);
        instructionRepository.deleteById(id);
        instructionSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/instructions?query=:query} : search for the instruction corresponding
     * to the query.
     *
     * @param query the query of the instruction search.
     * @return the result of the search.
     */
    @GetMapping("/_search/instructions")
    public List<Instruction> searchInstructions(@RequestParam String query) {
        log.debug("REST request to search Instructions for query {}", query);
        return StreamSupport.stream(instructionSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
