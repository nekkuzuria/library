package com.xtramile.library2024.web.rest;

import com.xtramile.library2024.repository.LibrarianRepository;
import com.xtramile.library2024.service.LibrarianService;
import com.xtramile.library2024.service.dto.LibrarianDTO;
import com.xtramile.library2024.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.xtramile.library2024.domain.Librarian}.
 */
@RestController
@RequestMapping("/api/librarians")
public class LibrarianResource {

    private static final Logger log = LoggerFactory.getLogger(LibrarianResource.class);

    private static final String ENTITY_NAME = "librarian";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LibrarianService librarianService;

    private final LibrarianRepository librarianRepository;

    public LibrarianResource(LibrarianService librarianService, LibrarianRepository librarianRepository) {
        this.librarianService = librarianService;
        this.librarianRepository = librarianRepository;
    }

    /**
     * {@code POST  /librarians} : Create a new librarian.
     *
     * @param librarianDTO the librarianDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new librarianDTO, or with status {@code 400 (Bad Request)} if the librarian has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LibrarianDTO> createLibrarian(@RequestBody LibrarianDTO librarianDTO) throws URISyntaxException {
        log.debug("REST request to save Librarian : {}", librarianDTO);
        if (librarianDTO.getId() != null) {
            throw new BadRequestAlertException("A new librarian cannot already have an ID", ENTITY_NAME, "idexists");
        }
        librarianDTO = librarianService.save(librarianDTO);
        return ResponseEntity.created(new URI("/api/librarians/" + librarianDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, librarianDTO.getId().toString()))
            .body(librarianDTO);
    }

    /**
     * {@code PUT  /librarians/:id} : Updates an existing librarian.
     *
     * @param id the id of the librarianDTO to save.
     * @param librarianDTO the librarianDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated librarianDTO,
     * or with status {@code 400 (Bad Request)} if the librarianDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the librarianDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LibrarianDTO> updateLibrarian(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody LibrarianDTO librarianDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Librarian : {}, {}", id, librarianDTO);
        if (librarianDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, librarianDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!librarianRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        librarianDTO = librarianService.update(librarianDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, librarianDTO.getId().toString()))
            .body(librarianDTO);
    }

    /**
     * {@code PATCH  /librarians/:id} : Partial updates given fields of an existing librarian, field will ignore if it is null
     *
     * @param id the id of the librarianDTO to save.
     * @param librarianDTO the librarianDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated librarianDTO,
     * or with status {@code 400 (Bad Request)} if the librarianDTO is not valid,
     * or with status {@code 404 (Not Found)} if the librarianDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the librarianDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LibrarianDTO> partialUpdateLibrarian(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody LibrarianDTO librarianDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Librarian partially : {}, {}", id, librarianDTO);
        if (librarianDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, librarianDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!librarianRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LibrarianDTO> result = librarianService.partialUpdate(librarianDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, librarianDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /librarians} : get all the librarians.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of librarians in body.
     */
    @GetMapping("")
    public List<LibrarianDTO> getAllLibrarians(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get all Librarians");
        return librarianService.findAll();
    }

    /**
     * {@code GET  /librarians/:id} : get the "id" librarian.
     *
     * @param id the id of the librarianDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the librarianDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LibrarianDTO> getLibrarian(@PathVariable("id") Long id) {
        log.debug("REST request to get Librarian : {}", id);
        Optional<LibrarianDTO> librarianDTO = librarianService.findOne(id);
        return ResponseUtil.wrapOrNotFound(librarianDTO);
    }

    /**
     * {@code DELETE  /librarians/:id} : delete the "id" librarian.
     *
     * @param id the id of the librarianDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLibrarian(@PathVariable("id") Long id) {
        log.debug("REST request to delete Librarian : {}", id);
        librarianService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
