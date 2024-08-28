package com.xtramile.library2024.web.rest;

import com.xtramile.library2024.repository.LibraryRepository;
import com.xtramile.library2024.service.LibraryService;
import com.xtramile.library2024.service.dto.LibraryDTO;
import com.xtramile.library2024.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.xtramile.library2024.domain.Library}.
 */
@RestController
@RequestMapping("/api/libraries")
public class LibraryResource {

    private static final Logger log = LoggerFactory.getLogger(LibraryResource.class);

    private static final String ENTITY_NAME = "library";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LibraryService libraryService;

    private final LibraryRepository libraryRepository;

    public LibraryResource(LibraryService libraryService, LibraryRepository libraryRepository) {
        this.libraryService = libraryService;
        this.libraryRepository = libraryRepository;
    }

    /**
     * {@code POST  /libraries} : Create a new library.
     *
     * @param libraryDTO the libraryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new libraryDTO, or with status {@code 400 (Bad Request)} if the library has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LibraryDTO> createLibrary(@RequestBody LibraryDTO libraryDTO) throws URISyntaxException {
        log.debug("REST request to save Library : {}", libraryDTO);
        if (libraryDTO.getId() != null) {
            log.error("Attempted to create a new library with an existing ID: {}", libraryDTO.getId());
            throw new BadRequestAlertException("A new library cannot already have an ID", ENTITY_NAME, "idexists");
        }
        libraryDTO = libraryService.save(libraryDTO);
        log.info("Library created successfully with ID: {}", libraryDTO.getId());
        return ResponseEntity.created(new URI("/api/libraries/" + libraryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, libraryDTO.getId().toString()))
            .body(libraryDTO);
    }

    /**
     * {@code PUT  /libraries/:id} : Updates an existing library.
     *
     * @param id the id of the libraryDTO to save.
     * @param libraryDTO the libraryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated libraryDTO,
     * or with status {@code 400 (Bad Request)} if the libraryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the libraryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LibraryDTO> updateLibrary(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody LibraryDTO libraryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Library : {}, {}", id, libraryDTO);
        if (libraryDTO.getId() == null) {
            log.error("Invalid ID: LibraryDTO ID is null");
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, libraryDTO.getId())) {
            log.error("ID mismatch: Path variable ID {} does not match LibraryDTO ID {}", id, libraryDTO.getId());
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!libraryRepository.existsById(id)) {
            log.error("Entity not found: No Library exists with ID {}", id);
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        libraryDTO = libraryService.update(libraryDTO);
        log.info("Library updated successfully with ID: {}", libraryDTO.getId());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, libraryDTO.getId().toString()))
            .body(libraryDTO);
    }

    /**
     * {@code PATCH  /libraries/:id} : Partial updates given fields of an existing library, field will ignore if it is null
     *
     * @param id the id of the libraryDTO to save.
     * @param libraryDTO the libraryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated libraryDTO,
     * or with status {@code 400 (Bad Request)} if the libraryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the libraryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the libraryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LibraryDTO> partialUpdateLibrary(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody LibraryDTO libraryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Library partially : {}, {}", id, libraryDTO);
        if (libraryDTO.getId() == null) {
            log.error("Invalid ID: LibraryDTO ID is null");
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, libraryDTO.getId())) {
            log.error("ID mismatch: Path variable ID {} does not match LibraryDTO ID {}", id, libraryDTO.getId());
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!libraryRepository.existsById(id)) {
            log.error("Entity not found: No Library exists with ID {}", id);
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LibraryDTO> result = libraryService.partialUpdate(libraryDTO);
        if (result.isPresent()) {
            log.info("Library partially updated with ID: {}", libraryDTO.getId());
        } else {
            log.warn("No Library updated with ID: {}", libraryDTO.getId());
        }

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, libraryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /libraries} : get all the libraries.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of libraries in body.
     */
    @GetMapping("")
    public ResponseEntity<List<LibraryDTO>> getAllLibraries(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Libraries");
        Page<LibraryDTO> page = libraryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        log.info("Retrieved {} Libraries", page.getTotalElements());
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /libraries/:id} : get the "id" library.
     *
     * @param id the id of the libraryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the libraryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LibraryDTO> getLibrary(@PathVariable("id") Long id) {
        log.debug("REST request to get Library : {}", id);
        Optional<LibraryDTO> libraryDTO = libraryService.findOne(id);
        if (libraryDTO.isPresent()) {
            log.info("Library found with ID: {}", id);
        } else {
            log.warn("Library not found with ID: {}", id);
        }
        return ResponseUtil.wrapOrNotFound(libraryDTO);
    }

    /**
     * {@code DELETE  /libraries/:id} : delete the "id" library.
     *
     * @param id the id of the libraryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLibrary(@PathVariable("id") Long id) {
        log.debug("REST request to delete Library : {}", id);
        if (libraryService.findOne(id).isPresent()) {
            libraryService.delete(id);
            log.info("Library deleted with ID: {}", id);
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                .build();
        } else {
            log.error("Attempted to delete non-existent Library with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
}
