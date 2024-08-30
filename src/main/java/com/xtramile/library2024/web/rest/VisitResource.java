package com.xtramile.library2024.web.rest;

import com.xtramile.library2024.repository.VisitRepository;
import com.xtramile.library2024.service.LibrarianService;
import com.xtramile.library2024.service.LibraryService;
import com.xtramile.library2024.service.VisitService;
import com.xtramile.library2024.service.dto.LibraryDTO;
import com.xtramile.library2024.service.dto.VisitDTO;
import com.xtramile.library2024.web.rest.errors.BadRequestAlertException;
import com.xtramile.library2024.web.rest.vm.VisitVM;
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
 * REST controller for managing {@link com.xtramile.library2024.domain.Visit}.
 */
@RestController
@RequestMapping("/api/visits")
public class VisitResource {

    private static final Logger log = LoggerFactory.getLogger(VisitResource.class);

    private static final String ENTITY_NAME = "visit";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VisitService visitService;

    private final VisitRepository visitRepository;

    private final LibraryService libraryService;

    public VisitResource(
        VisitService visitService,
        VisitRepository visitRepository,
        LibrarianService librarianService,
        LibraryService libraryService
    ) {
        this.visitService = visitService;
        this.visitRepository = visitRepository;
        this.libraryService = libraryService;
    }

    /**
     * {@code POST  /visits} : Create a new visit.
     *
     * @param visitDTO the visitDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new visitDTO, or with status {@code 400 (Bad Request)} if the visit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<VisitDTO> createVisit(@RequestBody VisitDTO visitDTO) throws URISyntaxException {
        log.debug("REST request to save Visit : {}", visitDTO);
        if (visitDTO.getId() != null) {
            log.error("Attempted to create a new visit with an existing ID: {}", visitDTO.getId());
            throw new BadRequestAlertException("A new visit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        visitDTO = visitService.save(visitDTO);
        log.info("Visit created successfully with ID: {}", visitDTO.getId());

        return ResponseEntity.created(new URI("/api/visits/" + visitDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, visitDTO.getId().toString()))
            .body(visitDTO);
    }

    /**
     * {@code PUT  /visits/:id} : Updates an existing visit.
     *
     * @param id the id of the visitDTO to save.
     * @param visitDTO the visitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated visitDTO,
     * or with status {@code 400 (Bad Request)} if the visitDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the visitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VisitDTO> updateVisit(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody VisitDTO visitDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Visit : {}, {}", id, visitDTO);

        if (visitDTO.getId() == null) {
            log.error("Invalid ID: Visit ID is null");
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, visitDTO.getId())) {
            log.error("ID mismatch: Path variable ID {} does not match Visit ID {}", id, visitDTO.getId());
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!visitRepository.existsById(id)) {
            log.error("Entity not found: No Visit exists with ID {}", id);
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        visitDTO = visitService.update(visitDTO);
        log.info("Visit updated successfully with ID: {}", visitDTO.getId());

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, visitDTO.getId().toString()))
            .body(visitDTO);
    }

    /**
     * {@code PATCH  /visits/:id} : Partial updates given fields of an existing visit, field will ignore if it is null
     *
     * @param id the id of the visitDTO to save.
     * @param visitDTO the visitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated visitDTO,
     * or with status {@code 400 (Bad Request)} if the visitDTO is not valid,
     * or with status {@code 404 (Not Found)} if the visitDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the visitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VisitDTO> partialUpdateVisit(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody VisitDTO visitDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Visit partially : {}, {}", id, visitDTO);
        if (visitDTO.getId() == null) {
            log.error("Invalid ID: Visit ID is null");
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, visitDTO.getId())) {
            log.error("ID mismatch: Path variable ID {} does not match Visit ID {}", id, visitDTO.getId());
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!visitRepository.existsById(id)) {
            log.error("Entity not found: No Visit exists with ID {}", id);
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VisitDTO> result = visitService.partialUpdate(visitDTO);
        log.info("Partial update result for Visit ID {}: {}", visitDTO.getId(), result);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, visitDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /visits} : get all the visits.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of visits in body.
     */
    @GetMapping("")
    public ResponseEntity<List<VisitDTO>> getAllVisits(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Visits");
        Page<VisitDTO> page = visitService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        log.info("Returning {} Visits with total elements: {}", page.getContent().size(), page.getTotalElements());
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/my-library")
    public ResponseEntity<List<VisitVM>> getCurrentUserLibraryVisits(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Visits of current user's library");

        LibraryDTO libraryDTO = libraryService.getLibraryOfCurrentLibrarian();
        if (libraryDTO == null) {
            log.error("No library found for the current librarian");
            return ResponseEntity.notFound().build();
        }

        Page<VisitVM> page = visitService.getVisitsForCurrentUserLibrary(libraryDTO, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        log.info(
            "Returning {} Visits for current user's library with total elements: {}",
            page.getContent().size(),
            page.getTotalElements()
        );
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /visits/:id} : get the "id" visit.
     *
     * @param id the id of the visitDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the visitDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VisitDTO> getVisit(@PathVariable("id") Long id) {
        log.debug("REST request to get Visit : {}", id);
        Optional<VisitDTO> visitDTO = visitService.findOne(id);

        if (visitDTO.isPresent()) {
            log.info("Found Visit with ID: {}", id);
        } else {
            log.warn("Visit with ID: {} not found", id);
        }

        return ResponseUtil.wrapOrNotFound(visitDTO);
    }

    /**
     * {@code DELETE  /visits/:id} : delete the "id" visit.
     *
     * @param id the id of the visitDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisit(@PathVariable("id") Long id) {
        log.debug("REST request to delete Visit : {}", id);

        if (visitService.findOne(id).isPresent()) {
            visitService.delete(id);
            log.info("Successfully deleted Visit with ID: {}", id);
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                .build();
        } else {
            log.error("Attempted to delete non-existent Visit with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
}
