package com.xtramile.library2024.service;

import com.xtramile.library2024.service.dto.VisitorDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.xtramile.library2024.domain.Visitor}.
 */
public interface VisitorService {
    /**
     * Save a visitor.
     *
     * @param visitorDTO the entity to save.
     * @return the persisted entity.
     */
    VisitorDTO save(VisitorDTO visitorDTO);

    /**
     * Updates a visitor.
     *
     * @param visitorDTO the entity to update.
     * @return the persisted entity.
     */
    VisitorDTO update(VisitorDTO visitorDTO);

    /**
     * Partially updates a visitor.
     *
     * @param visitorDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<VisitorDTO> partialUpdate(VisitorDTO visitorDTO);

    /**
     * Get all the visitors.
     *
     * @return the list of entities.
     */
    List<VisitorDTO> findAll();

    /**
     * Get the "id" visitor.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VisitorDTO> findOne(Long id);

    /**
     * Delete the "id" visitor.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
