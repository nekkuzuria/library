package com.xtramile.library2024.web.rest;

import com.xtramile.library2024.domain.Visitor;
import com.xtramile.library2024.repository.VisitorBookStorageRepository;
import com.xtramile.library2024.service.VisitorBookStorageService;
import com.xtramile.library2024.service.VisitorService;
import com.xtramile.library2024.service.dto.VisitorBookStorageDTO;
import com.xtramile.library2024.service.dto.VisitorDTO;
import com.xtramile.library2024.web.rest.errors.BadRequestAlertException;
import com.xtramile.library2024.web.rest.vm.VisitorBookStorageVM;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.xtramile.library2024.domain.VisitorBookStorage}.
 */
@RestController
@RequestMapping("/api/visitor-book-storages")
public class VisitorBookStorageResource {

    private static final Logger log = LoggerFactory.getLogger(VisitorBookStorageResource.class);

    private static final String ENTITY_NAME = "visitorBookStorage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VisitorBookStorageService visitorBookStorageService;

    private final VisitorBookStorageRepository visitorBookStorageRepository;

    private final VisitorService visitorService;

    public VisitorBookStorageResource(
        VisitorBookStorageService visitorBookStorageService,
        VisitorBookStorageRepository visitorBookStorageRepository,
        VisitorService visitorService
    ) {
        this.visitorBookStorageService = visitorBookStorageService;
        this.visitorBookStorageRepository = visitorBookStorageRepository;
        this.visitorService = visitorService;
    }

    /**
     * {@code POST  /visitor-book-storages} : Create a new visitorBookStorage.
     *
     * @param visitorBookStorageDTO the visitorBookStorageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new visitorBookStorageDTO, or with status {@code 400 (Bad Request)} if the visitorBookStorage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<VisitorBookStorageDTO> createVisitorBookStorage(@RequestBody VisitorBookStorageDTO visitorBookStorageDTO)
        throws URISyntaxException {
        log.debug("REST request to save VisitorBookStorage : {}", visitorBookStorageDTO);
        if (visitorBookStorageDTO.getId() != null) {
            log.error("Attempted to create a new VisitorBookStorage with an existing ID: {}", visitorBookStorageDTO.getId());
            throw new BadRequestAlertException("A new visitorBookStorage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        visitorBookStorageDTO = visitorBookStorageService.save(visitorBookStorageDTO);
        log.info("VisitorBookStorage created successfully with ID: {}", visitorBookStorageDTO.getId());

        return ResponseEntity.created(new URI("/api/visitor-book-storages/" + visitorBookStorageDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, visitorBookStorageDTO.getId().toString()))
            .body(visitorBookStorageDTO);
    }

    /**
     * {@code PUT  /visitor-book-storages/:id} : Updates an existing visitorBookStorage.
     *
     * @param id the id of the visitorBookStorageDTO to save.
     * @param visitorBookStorageDTO the visitorBookStorageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated visitorBookStorageDTO,
     * or with status {@code 400 (Bad Request)} if the visitorBookStorageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the visitorBookStorageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VisitorBookStorageDTO> updateVisitorBookStorage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody VisitorBookStorageDTO visitorBookStorageDTO
    ) throws URISyntaxException {
        log.debug("REST request to update VisitorBookStorage : {}, {}", id, visitorBookStorageDTO);
        if (visitorBookStorageDTO.getId() == null) {
            log.error("Invalid ID: VisitorBookStorage ID is null");
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, visitorBookStorageDTO.getId())) {
            log.error("ID mismatch: Path variable ID {} does not match VisitorBookStorage ID {}", id, visitorBookStorageDTO.getId());
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!visitorBookStorageRepository.existsById(id)) {
            log.error("Entity not found: No VisitorBookStorage exists with ID {}", id);
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        visitorBookStorageDTO = visitorBookStorageService.update(visitorBookStorageDTO);
        log.info("VisitorBookStorage updated successfully with ID: {}", visitorBookStorageDTO.getId());

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, visitorBookStorageDTO.getId().toString()))
            .body(visitorBookStorageDTO);
    }

    /**
     * {@code PATCH  /visitor-book-storages/:id} : Partial updates given fields of an existing visitorBookStorage, field will ignore if it is null
     *
     * @param id the id of the visitorBookStorageDTO to save.
     * @param visitorBookStorageDTO the visitorBookStorageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated visitorBookStorageDTO,
     * or with status {@code 400 (Bad Request)} if the visitorBookStorageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the visitorBookStorageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the visitorBookStorageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VisitorBookStorageDTO> partialUpdateVisitorBookStorage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody VisitorBookStorageDTO visitorBookStorageDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update VisitorBookStorage partially : {}, {}", id, visitorBookStorageDTO);
        if (visitorBookStorageDTO.getId() == null) {
            log.error("Invalid ID: VisitorBookStorage ID is null");
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, visitorBookStorageDTO.getId())) {
            log.error("ID mismatch: Path variable ID {} does not match VisitorBookStorage ID {}", id, visitorBookStorageDTO.getId());
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!visitorBookStorageRepository.existsById(id)) {
            log.error("Entity not found: No VisitorBookStorage exists with ID {}", id);
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VisitorBookStorageDTO> result = visitorBookStorageService.partialUpdate(visitorBookStorageDTO);
        log.info("Partial update result for VisitorBookStorage ID {}: {}", visitorBookStorageDTO.getId(), result);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, visitorBookStorageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /visitor-book-storages} : get all the visitorBookStorages.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of visitorBookStorages in body.
     */
    @GetMapping("")
    public ResponseEntity<List<VisitorBookStorageDTO>> getAllVisitorBookStorages(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of VisitorBookStorages");
        Page<VisitorBookStorageDTO> page = visitorBookStorageService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        log.info("Returning {} VisitorBookStorages with total elements: {}", page.getContent().size(), page.getTotalElements());
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/my")
    public ResponseEntity<List<VisitorBookStorageVM>> getCurrentUserBookStorages(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of VisitorBookStorages of current user");

        VisitorDTO visitorDTO = visitorService.getVisitorOfCurrentUser();
        if (visitorDTO == null) {
            log.error("No Visitor found for the current user");
            return ResponseEntity.notFound().build();
        }

        Page<VisitorBookStorageVM> page = visitorBookStorageService.getVisitorBookStoragesForCurrentUser(visitorDTO, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        log.info(
            "Returning {} VisitorBookStorages for current user with total elements: {}",
            page.getContent().size(),
            page.getTotalElements()
        );
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /visitor-book-storages/:id} : get the "id" visitorBookStorage.
     *
     * @param id the id of the visitorBookStorageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the visitorBookStorageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VisitorBookStorageDTO> getVisitorBookStorage(@PathVariable("id") Long id) {
        log.debug("REST request to get VisitorBookStorage : {}", id);
        Optional<VisitorBookStorageDTO> visitorBookStorageDTO = visitorBookStorageService.findOne(id);

        if (visitorBookStorageDTO.isPresent()) {
            log.info("Found VisitorBookStorage with ID: {}", id);
        } else {
            log.warn("VisitorBookStorage with ID: {} not found", id);
        }

        return ResponseUtil.wrapOrNotFound(visitorBookStorageDTO);
    }

    /**
     * {@code DELETE  /visitor-book-storages/:id} : delete the "id" visitorBookStorage.
     *
     * @param id the id of the visitorBookStorageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisitorBookStorage(@PathVariable("id") Long id) {
        log.debug("REST request to delete VisitorBookStorage : {}", id);
        if (visitorBookStorageService.findOne(id).isPresent()) {
            visitorBookStorageService.delete(id);
            log.info("Successfully deleted VisitorBookStorage with ID: {}", id);
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                .build();
        } else {
            log.error("Visitor not found for deletion with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
}
