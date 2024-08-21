package com.xtramile.library2024.service;

import com.xtramile.library2024.domain.PendingTask;
import com.xtramile.library2024.repository.PendingTaskRepository;
import com.xtramile.library2024.service.dto.PendingTaskDTO;
import com.xtramile.library2024.service.mapper.LibraryMapper;
import com.xtramile.library2024.service.mapper.PendingTaskMapper;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PendingTaskService {

    private static final Logger log = LoggerFactory.getLogger(PendingTaskService.class);

    private final PendingTaskRepository pendingTaskRepository;

    private final PendingTaskMapper pendingTaskMapper;

    private final LibraryMapper libraryMapper;

    public PendingTaskService(
        PendingTaskRepository pendingTaskRepository,
        PendingTaskMapper pendingTaskMapper,
        LibraryMapper libraryMapper
    ) {
        this.pendingTaskRepository = pendingTaskRepository;
        this.pendingTaskMapper = pendingTaskMapper;
        this.libraryMapper = libraryMapper;
    }

    /**
     * Save a pendingTask.
     *
     * @param pendingTaskDTO the entity to save.
     * @return the persisted entity.
     */
    public PendingTaskDTO save(PendingTaskDTO pendingTaskDTO) {
        log.debug("Request to save PendingTask : {}", pendingTaskDTO);
        PendingTask pendingTask = pendingTaskMapper.toEntity(pendingTaskDTO);
        pendingTask = pendingTaskRepository.save(pendingTask);
        return pendingTaskMapper.toDto(pendingTask);
    }

    /**
     * Update a pendingTask.
     *
     * @param pendingTaskDTO the entity to save.
     * @return the persisted entity.
     */
    public PendingTaskDTO update(PendingTaskDTO pendingTaskDTO) {
        log.debug("Request to update PendingTask : {}", pendingTaskDTO);
        PendingTask pendingTask = pendingTaskMapper.toEntity(pendingTaskDTO);
        pendingTask = pendingTaskRepository.save(pendingTask);
        return pendingTaskMapper.toDto(pendingTask);
    }

    /**
     * Partially update a pendingTask.
     *
     * @param pendingTaskDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PendingTaskDTO> partialUpdate(PendingTaskDTO pendingTaskDTO) {
        log.debug("Request to partially update PendingTask : {}", pendingTaskDTO);

        return pendingTaskRepository
            .findById(pendingTaskDTO.getId())
            .map(existingPendingTask -> {
                pendingTaskMapper.partialUpdate(existingPendingTask, pendingTaskDTO);

                return existingPendingTask;
            })
            .map(pendingTaskRepository::save)
            .map(pendingTaskMapper::toDto);
    }

    /**
     * Get all the pendingTasks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Page<PendingTaskDTO> findAll(Pageable pageable) {
        log.debug("Request to get all PendingTasks");
        return pendingTaskRepository.findAll(pageable).map(pendingTaskMapper::toDto);
    }

    /**
     * Get one pendingTask by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Optional<PendingTaskDTO> findOne(Long id) {
        log.debug("Request to get PendingTask : {}", id);
        return pendingTaskRepository.findById(id).map(pendingTaskMapper::toDto);
    }

    /**
     * Delete the pendingTask by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete PendingTask : {}", id);
        pendingTaskRepository.deleteById(id);
    }
}
