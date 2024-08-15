package com.xtramile.library2024.service;

import com.xtramile.library2024.service.dto.LibrarianDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.xtramile.library2024.domain.Librarian}.
 */
public interface LibrarianService {
    /**
     * Save a librarian.
     *
     * @param librarianDTO the entity to save.
     * @return the persisted entity.
     */
    LibrarianDTO save(LibrarianDTO librarianDTO);

    /**
     * Updates a librarian.
     *
     * @param librarianDTO the entity to update.
     * @return the persisted entity.
     */
    LibrarianDTO update(LibrarianDTO librarianDTO);

    /**
     * Partially updates a librarian.
     *
     * @param librarianDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<LibrarianDTO> partialUpdate(LibrarianDTO librarianDTO);

    /**
     * Get all the librarians.
     *
     * @return the list of entities.
     */
    List<LibrarianDTO> findAll();

    /**
     * Get all the librarians with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<LibrarianDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" librarian.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LibrarianDTO> findOne(Long id);

    /**
     * Delete the "id" librarian.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
