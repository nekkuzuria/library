package com.xtramile.library2024.service;

import com.xtramile.library2024.service.dto.LibrarianDTO;
import java.util.List;
import java.util.Optional;

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
