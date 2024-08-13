package com.xtramile.library2024.service;

import com.xtramile.library2024.domain.Visit;
import com.xtramile.library2024.repository.VisitRepository;
import com.xtramile.library2024.service.dto.VisitDTO;
import com.xtramile.library2024.service.mapper.VisitMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.xtramile.library2024.domain.Visit}.
 */
@Service
@Transactional
public class VisitService {

    private static final Logger log = LoggerFactory.getLogger(VisitService.class);

    private final VisitRepository visitRepository;

    private final VisitMapper visitMapper;

    public VisitService(VisitRepository visitRepository, VisitMapper visitMapper) {
        this.visitRepository = visitRepository;
        this.visitMapper = visitMapper;
    }

    /**
     * Save a visit.
     *
     * @param visitDTO the entity to save.
     * @return the persisted entity.
     */
    public VisitDTO save(VisitDTO visitDTO) {
        log.debug("Request to save Visit : {}", visitDTO);
        Visit visit = visitMapper.toEntity(visitDTO);
        visit = visitRepository.save(visit);
        return visitMapper.toDto(visit);
    }

    /**
     * Update a visit.
     *
     * @param visitDTO the entity to save.
     * @return the persisted entity.
     */
    public VisitDTO update(VisitDTO visitDTO) {
        log.debug("Request to update Visit : {}", visitDTO);
        Visit visit = visitMapper.toEntity(visitDTO);
        visit = visitRepository.save(visit);
        return visitMapper.toDto(visit);
    }

    /**
     * Partially update a visit.
     *
     * @param visitDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<VisitDTO> partialUpdate(VisitDTO visitDTO) {
        log.debug("Request to partially update Visit : {}", visitDTO);

        return visitRepository
            .findById(visitDTO.getId())
            .map(existingVisit -> {
                visitMapper.partialUpdate(existingVisit, visitDTO);

                return existingVisit;
            })
            .map(visitRepository::save)
            .map(visitMapper::toDto);
    }

    /**
     * Get all the visits.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<VisitDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Visits");
        return visitRepository.findAll(pageable).map(visitMapper::toDto);
    }

    /**
     * Get one visit by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<VisitDTO> findOne(Long id) {
        log.debug("Request to get Visit : {}", id);
        return visitRepository.findById(id).map(visitMapper::toDto);
    }

    /**
     * Delete the visit by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Visit : {}", id);
        visitRepository.deleteById(id);
    }
}
