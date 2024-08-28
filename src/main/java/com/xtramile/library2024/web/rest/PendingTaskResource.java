package com.xtramile.library2024.web.rest;

import com.xtramile.library2024.repository.PendingTaskRepository;
import com.xtramile.library2024.service.PendingTaskService;
import com.xtramile.library2024.service.dto.PendingTaskDTO;
import com.xtramile.library2024.web.rest.errors.BadRequestAlertException;
import com.xtramile.library2024.web.rest.vm.PendingTaskVM;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/pending-tasks")
public class PendingTaskResource {

    private static final Logger log = LoggerFactory.getLogger(PendingTaskResource.class);

    private static final String ENTITY_NAME = "pendingTask";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PendingTaskService pendingTaskService;

    private final PendingTaskRepository pendingTaskRepository;

    public PendingTaskResource(PendingTaskService pendingTaskService, PendingTaskRepository pendingTaskRepository) {
        this.pendingTaskService = pendingTaskService;
        this.pendingTaskRepository = pendingTaskRepository;
    }

    /**
     * {@code POST  /pending-task} : Create a new pendingTask.
     *
     * @param pendingTaskVM the pendingTaskDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pendingTaskDTO, or with status {@code 400 (Bad Request)} if the pendingTask has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PendingTaskVM> createPendingTask(@RequestBody PendingTaskVM pendingTaskVM) throws URISyntaxException {
        log.debug("REST request to save PendingTask : {}", pendingTaskVM);
        if (pendingTaskVM.getId() != null) {
            log.warn("Attempt to create a new pendingTask with an existing ID: {}", pendingTaskVM.getId());
            throw new BadRequestAlertException("A new pendingTask cannot already have an ID", ENTITY_NAME, "idexists");
        }
        pendingTaskVM = pendingTaskService.createNew(pendingTaskVM);
        log.info("PendingTask created with ID: {}", pendingTaskVM.getId());
        return ResponseEntity.created(new URI("/api/pending-task/" + pendingTaskVM.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, pendingTaskVM.getId().toString()))
            .body(pendingTaskVM);
    }

    /**
     * {@code PUT  /pending-task/:id} : Updates an existing pendingTask.
     *
     * @param id the id of the pendingTaskDTO to save.
     * @param pendingTaskDTO the pendingTaskDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pendingTaskDTO,
     * or with status {@code 400 (Bad Request)} if the pendingTaskDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pendingTaskDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PendingTaskDTO> updatePendingTask(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PendingTaskDTO pendingTaskDTO
    ) throws URISyntaxException {
        log.debug("REST request to update PendingTask : {}, {}", id, pendingTaskDTO);

        if (pendingTaskDTO.getId() == null) {
            log.error("Invalid ID: PendingTask ID is null");
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pendingTaskDTO.getId())) {
            log.error("ID mismatch: Path variable ID {} does not match PendingTask ID {}", id, pendingTaskDTO.getId());
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pendingTaskRepository.existsById(id)) {
            log.error("Entity not found: No PendingTask exists with ID {}", id);
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        pendingTaskDTO = pendingTaskService.update(pendingTaskDTO);
        log.info("PendingTask updated successfully with ID: {}", pendingTaskDTO.getId());

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pendingTaskDTO.getId().toString()))
            .body(pendingTaskDTO);
    }

    /**
     * {@code PATCH  /pending-task/:id} : Partial updates given fields of an existing pendingTask, field will ignore if it is null
     *
     * @param id the id of the pendingTaskDTO to save.
     * @param pendingTaskDTO the pendingTaskDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pendingTaskDTO,
     * or with status {@code 400 (Bad Request)} if the pendingTaskDTO is not valid,
     * or with status {@code 404 (Not Found)} if the pendingTaskDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the pendingTaskDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PendingTaskVM> partialUpdatePendingTask(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PendingTaskDTO pendingTaskDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update PendingTask partially : {}, {}", id, pendingTaskDTO);
        if (pendingTaskDTO.getId() == null) {
            log.error("Invalid ID: PendingTask ID is null");
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pendingTaskDTO.getId())) {
            log.error("ID mismatch: Path variable ID {} does not match PendingTask ID {}", id, pendingTaskDTO.getId());
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pendingTaskRepository.existsById(id)) {
            log.error("Entity not found: No PendingTask exists with ID {}", id);
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PendingTaskVM> result = pendingTaskService.processPendingTask(pendingTaskDTO);
        log.info("PendingTask partially updated with ID: {}", pendingTaskDTO.getId());

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pendingTaskDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /pending-task} : get all the pendingTasks.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pendingTasks in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PendingTaskVM>> getAllPendingTasksOfCurrentLibrary(
        @org.springdoc.core.annotations.ParameterObject @PageableDefault(
            sort = "createdDate",
            direction = Sort.Direction.DESC
        ) Pageable pageable
    ) {
        log.debug("REST request to get a page of PendingTasks");
        Page<PendingTaskVM> page = pendingTaskService.findAllFromCurrentLibrary(pageable);
        if (page.hasContent()) {
            log.info("Retrieved {} pending tasks for the current library", page.getTotalElements());
        } else {
            log.warn("No pending tasks found for the current library");
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /pending-task/:id} : get the "id" pendingTask.
     *
     * @param id the id of the pendingTaskDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pendingTaskDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PendingTaskDTO> getPendingTask(@PathVariable("id") Long id) {
        log.debug("REST request to get PendingTask : {}", id);
        Optional<PendingTaskDTO> pendingTaskDTO = pendingTaskService.findOne(id);
        if (pendingTaskDTO.isPresent()) {
            log.info("Retrieved PendingTask with ID: {}", id);
        } else {
            log.warn("PendingTask not found with ID: {}", id);
        }
        return ResponseUtil.wrapOrNotFound(pendingTaskDTO);
    }

    /**
     * {@code DELETE  /pending-task/:id} : delete the "id" pendingTask.
     *
     * @param id the id of the pendingTaskDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePendingTask(@PathVariable("id") Long id) {
        log.debug("REST request to delete PendingTask : {}", id);
        if (pendingTaskRepository.existsById(id)) {
            pendingTaskService.delete(id);
            log.info("PendingTask deleted with ID: {}", id);
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                .build();
        } else {
            log.warn("PendingTask not found for deletion with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<PendingTaskVM>> getAllPendingTasksOfVisitor(
        @org.springdoc.core.annotations.ParameterObject @PageableDefault(
            sort = "createdDate",
            direction = Sort.Direction.ASC
        ) Pageable pageable
    ) {
        log.debug("REST request to get a page of PendingTasks");
        Page<PendingTaskVM> page = pendingTaskService.findAllFromCurrentVisitor(pageable);
        if (page.hasContent()) {
            log.info("Retrieved {} pending tasks for the current visitor", page.getTotalElements());
        } else {
            log.warn("No pending tasks found for the current visitor");
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
