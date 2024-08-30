package com.xtramile.library2024.service;

import com.xtramile.library2024.domain.User;
import com.xtramile.library2024.service.dto.AdminUserDTO;
import com.xtramile.library2024.service.dto.VisitorDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    VisitorDTO update(VisitorDTO visitorDTO, User user, AdminUserDTO userDTO);

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
     * Get all the visitors with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VisitorDTO> findAllWithEagerRelationships(Pageable pageable);

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

    Long getVisitorIdOfCurrentUser();

    VisitorDTO getVisitorOfCurrentUser();
}
