package com.xtramile.library2024.web.rest;

import com.xtramile.library2024.repository.BookStorageRepository;
import com.xtramile.library2024.service.BookStorageService;
import com.xtramile.library2024.service.dto.BookStorageDTO;
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
 * REST controller for managing {@link com.xtramile.library2024.domain.BookStorage}.
 */
@RestController
@RequestMapping("/api/book-storages")
public class BookStorageResource {

    private static final Logger log = LoggerFactory.getLogger(BookStorageResource.class);

    private static final String ENTITY_NAME = "bookStorage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BookStorageService bookStorageService;

    private final BookStorageRepository bookStorageRepository;

    public BookStorageResource(BookStorageService bookStorageService, BookStorageRepository bookStorageRepository) {
        this.bookStorageService = bookStorageService;
        this.bookStorageRepository = bookStorageRepository;
    }

    /**
     * {@code POST  /book-storages} : Create a new bookStorage.
     *
     * @param bookStorageDTO the bookStorageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bookStorageDTO, or with status {@code 400 (Bad Request)} if the bookStorage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BookStorageDTO> createBookStorage(@RequestBody BookStorageDTO bookStorageDTO) throws URISyntaxException {
        log.debug("REST request to save BookStorage : {}", bookStorageDTO);
        if (bookStorageDTO.getId() != null) {
            throw new BadRequestAlertException("A new bookStorage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        bookStorageDTO = bookStorageService.save(bookStorageDTO);
        return ResponseEntity.created(new URI("/api/book-storages/" + bookStorageDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, bookStorageDTO.getId().toString()))
            .body(bookStorageDTO);
    }

    /**
     * {@code PUT  /book-storages/:id} : Updates an existing bookStorage.
     *
     * @param id the id of the bookStorageDTO to save.
     * @param bookStorageDTO the bookStorageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookStorageDTO,
     * or with status {@code 400 (Bad Request)} if the bookStorageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bookStorageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookStorageDTO> updateBookStorage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody BookStorageDTO bookStorageDTO
    ) throws URISyntaxException {
        log.debug("REST request to update BookStorage : {}, {}", id, bookStorageDTO);
        if (bookStorageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookStorageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookStorageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        bookStorageDTO = bookStorageService.update(bookStorageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bookStorageDTO.getId().toString()))
            .body(bookStorageDTO);
    }

    /**
     * {@code PATCH  /book-storages/:id} : Partial updates given fields of an existing bookStorage, field will ignore if it is null
     *
     * @param id the id of the bookStorageDTO to save.
     * @param bookStorageDTO the bookStorageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookStorageDTO,
     * or with status {@code 400 (Bad Request)} if the bookStorageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the bookStorageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the bookStorageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BookStorageDTO> partialUpdateBookStorage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody BookStorageDTO bookStorageDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update BookStorage partially : {}, {}", id, bookStorageDTO);
        if (bookStorageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookStorageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookStorageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BookStorageDTO> result = bookStorageService.partialUpdate(bookStorageDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bookStorageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /book-storages} : get all the bookStorages.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bookStorages in body.
     */
    @GetMapping("")
    public ResponseEntity<List<BookStorageDTO>> getAllBookStorages(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of BookStorages");
        Page<BookStorageDTO> page = bookStorageService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /book-storages/:id} : get the "id" bookStorage.
     *
     * @param id the id of the bookStorageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bookStorageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookStorageDTO> getBookStorage(@PathVariable("id") Long id) {
        log.debug("REST request to get BookStorage : {}", id);
        Optional<BookStorageDTO> bookStorageDTO = bookStorageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bookStorageDTO);
    }

    /**
     * {@code DELETE  /book-storages/:id} : delete the "id" bookStorage.
     *
     * @param id the id of the bookStorageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookStorage(@PathVariable("id") Long id) {
        log.debug("REST request to delete BookStorage : {}", id);
        bookStorageService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
