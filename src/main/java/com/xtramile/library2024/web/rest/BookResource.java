package com.xtramile.library2024.web.rest;

import com.xtramile.library2024.repository.BookRepository;
import com.xtramile.library2024.service.BookService;
import com.xtramile.library2024.service.BookStorageService;
import com.xtramile.library2024.service.dto.BookDTO;
import com.xtramile.library2024.service.mapper.BookMapper;
import com.xtramile.library2024.web.rest.errors.BadRequestAlertException;
import com.xtramile.library2024.web.rest.vm.BookVM;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
 * REST controller for managing {@link com.xtramile.library2024.domain.Book}.
 */
@RestController
@RequestMapping("/api/books")
public class BookResource {

    private static final Logger log = LoggerFactory.getLogger(BookResource.class);

    private static final String ENTITY_NAME = "book";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BookService bookService;

    private final BookRepository bookRepository;

    public BookResource(BookService bookService, BookRepository bookRepository) {
        this.bookService = bookService;
        this.bookRepository = bookRepository;
    }

    /**
     * {@code POST  /books} : Create a new book.
     *
     * @param bookVm the bookVm to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bookDTO, or with status {@code 400 (Bad Request)} if the book has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BookVM> createBook(@Valid @RequestBody BookVM bookVm) throws URISyntaxException {
        log.debug("REST request to save Book : {}", bookVm);
        if (bookVm.getId() != null) {
            throw new BadRequestAlertException("A new book cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BookVM savedBookVM = bookService.createBook(bookVm);

        return ResponseEntity.created(new URI("/api/books/" + savedBookVM.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, savedBookVM.getId().toString()))
            .body(savedBookVM);
    }

    /**
     * {@code PUT  /books/:id} : Updates an existing book.
     *
     * @param id the id of the bookDTO to save.
     * @param bookVM the bookVM to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookDTO,
     * or with status {@code 400 (Bad Request)} if the bookDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bookDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BookVM bookVM
    ) throws URISyntaxException {
        log.debug("REST request to update Book : {}, {}", id, bookVM);
        if (bookVM.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookVM.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BookDTO bookDTO = bookService.update(bookVM);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bookDTO.getId().toString()))
            .body(bookDTO);
    }

    /**
     * {@code PATCH  /books/:id} : Partial updates given fields of an existing book, field will ignore if it is null
     *
     * @param id the id of the bookDTO to save.
     * @param bookVM the bookVM to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookDTO,
     * or with status {@code 400 (Bad Request)} if the bookDTO is not valid,
     * or with status {@code 404 (Not Found)} if the bookDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the bookDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BookDTO> partialUpdateBook(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BookVM bookVM
    ) throws URISyntaxException {
        log.debug("REST request to partial update Book partially : {}, {}", id, bookVM);
        if (bookVM.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookVM.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BookDTO> result = bookService.partialUpdate(bookVM);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bookVM.getId().toString())
        );
    }

    /**
     * {@code GET  /books} : get all the books.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of books in body.
     */
    @GetMapping("")
    public ResponseEntity<List<BookVM>> getAllBooksOfCurrentLibrary(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "filter", required = false) String filter
    ) {
        log.debug("REST request to get a page of Books");
        Page<BookVM> page = bookService.getBooksForCurrentUserLibrary(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /books/:id} : get the "id" book.
     *
     * @param id the id of the bookDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bookDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookVM> getBook(@PathVariable("id") Long id) {
        log.debug("REST request to get Book : {}", id);
        Optional<BookVM> bookVM = bookService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bookVM);
    }

    /**
     * {@code DELETE  /books/:id} : delete the "id" book.
     *
     * @param id the id of the bookDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable("id") Long id) {
        log.debug("REST request to delete Book : {}", id);
        bookService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
