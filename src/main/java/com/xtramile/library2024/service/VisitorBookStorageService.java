package com.xtramile.library2024.service;

import com.xtramile.library2024.domain.Visitor;
import com.xtramile.library2024.domain.VisitorBookStorage;
import com.xtramile.library2024.repository.VisitorBookStorageRepository;
import com.xtramile.library2024.service.dto.VisitorBookStorageDTO;
import com.xtramile.library2024.service.dto.VisitorDTO;
import com.xtramile.library2024.service.mapper.VisitorBookStorageMapper;
import com.xtramile.library2024.service.mapper.VisitorMapper;
import com.xtramile.library2024.web.rest.vm.VisitorBookStorageVM;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.xtramile.library2024.domain.VisitorBookStorage}.
 */
@Service
@Transactional
public class VisitorBookStorageService {

    private static final Logger log = LoggerFactory.getLogger(VisitorBookStorageService.class);

    private final VisitorBookStorageRepository visitorBookStorageRepository;

    private final VisitorBookStorageMapper visitorBookStorageMapper;
    private final VisitorMapper visitorMapper;

    public VisitorBookStorageService(
        VisitorBookStorageRepository visitorBookStorageRepository,
        VisitorBookStorageMapper visitorBookStorageMapper,
        VisitorMapper visitorMapper
    ) {
        this.visitorBookStorageRepository = visitorBookStorageRepository;
        this.visitorBookStorageMapper = visitorBookStorageMapper;
        this.visitorMapper = visitorMapper;
    }

    /**
     * Save a visitorBookStorage.
     *
     * @param visitorBookStorageDTO the entity to save.
     * @return the persisted entity.
     */
    public VisitorBookStorageDTO save(VisitorBookStorageDTO visitorBookStorageDTO) {
        log.debug("Request to save VisitorBookStorage : {}", visitorBookStorageDTO);
        VisitorBookStorage visitorBookStorage = visitorBookStorageMapper.toEntity(visitorBookStorageDTO);
        visitorBookStorage = visitorBookStorageRepository.save(visitorBookStorage);
        return visitorBookStorageMapper.toDto(visitorBookStorage);
    }

    /**
     * Update a visitorBookStorage.
     *
     * @param visitorBookStorageDTO the entity to save.
     * @return the persisted entity.
     */
    public VisitorBookStorageDTO update(VisitorBookStorageDTO visitorBookStorageDTO) {
        log.debug("Request to update VisitorBookStorage : {}", visitorBookStorageDTO);
        VisitorBookStorage visitorBookStorage = visitorBookStorageMapper.toEntity(visitorBookStorageDTO);
        visitorBookStorage = visitorBookStorageRepository.save(visitorBookStorage);
        return visitorBookStorageMapper.toDto(visitorBookStorage);
    }

    /**
     * Partially update a visitorBookStorage.
     *
     * @param visitorBookStorageDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<VisitorBookStorageDTO> partialUpdate(VisitorBookStorageDTO visitorBookStorageDTO) {
        log.debug("Request to partially update VisitorBookStorage : {}", visitorBookStorageDTO);

        return visitorBookStorageRepository
            .findById(visitorBookStorageDTO.getId())
            .map(existingVisitorBookStorage -> {
                visitorBookStorageMapper.partialUpdate(existingVisitorBookStorage, visitorBookStorageDTO);

                return existingVisitorBookStorage;
            })
            .map(visitorBookStorageRepository::save)
            .map(visitorBookStorageMapper::toDto);
    }

    /**
     * Get all the visitorBookStorages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<VisitorBookStorageDTO> findAll(Pageable pageable) {
        log.debug("Request to get all VisitorBookStorages");
        return visitorBookStorageRepository.findAll(pageable).map(visitorBookStorageMapper::toDto);
    }

    /**
     * Get one visitorBookStorage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<VisitorBookStorageDTO> findOne(Long id) {
        log.debug("Request to get VisitorBookStorage : {}", id);
        return visitorBookStorageRepository.findById(id).map(visitorBookStorageMapper::toDto);
    }

    /**
     * Delete the visitorBookStorage by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete VisitorBookStorage : {}", id);
        visitorBookStorageRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<VisitorBookStorageVM> getVisitorBookStoragesForCurrentUser(VisitorDTO visitorDTO, Pageable pageable) {
        log.debug("Request to get book storages for visitor with ID: {}", visitorDTO.getId());

        Visitor visitor = visitorMapper.toEntity(visitorDTO);
        log.debug("Converted VisitorDTO to Visitor entity: {}", visitor);

        Page<VisitorBookStorageVM> bookStoragesPage = visitorBookStorageRepository
            .findByVisitor(visitor, pageable)
            .map(visitorBookStorageMapper::toVm);

        log.debug("Retrieved {} book storages for visitor with ID: {}", bookStoragesPage.getTotalElements(), visitorDTO.getId());
        return bookStoragesPage;
    }
}
