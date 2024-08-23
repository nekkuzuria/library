package com.xtramile.library2024.service;

import com.xtramile.library2024.domain.PendingTask;
import com.xtramile.library2024.domain.enumeration.PendingTaskStatus;
import com.xtramile.library2024.repository.BookRepository;
import com.xtramile.library2024.repository.PendingTaskRepository;
import com.xtramile.library2024.service.dto.BookDTO;
import com.xtramile.library2024.service.dto.PendingTaskDTO;
import com.xtramile.library2024.service.mapper.BookMapper;
import com.xtramile.library2024.service.mapper.LibraryMapper;
import com.xtramile.library2024.service.mapper.PendingTaskMapper;
import com.xtramile.library2024.web.rest.vm.PendingTaskVM;
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

    LibraryService libraryService;

    VisitorService visitorService;

    BookService bookService;

    BookMapper bookMapper;
    private final BookRepository bookRepository;

    public PendingTaskService(
        PendingTaskRepository pendingTaskRepository,
        PendingTaskMapper pendingTaskMapper,
        LibraryMapper libraryMapper,
        LibraryService libraryService,
        VisitorService visitorService,
        BookService bookService,
        BookRepository bookRepository,
        BookMapper bookMapper
    ) {
        this.pendingTaskRepository = pendingTaskRepository;
        this.pendingTaskMapper = pendingTaskMapper;
        this.libraryMapper = libraryMapper;
        this.libraryService = libraryService;
        this.visitorService = visitorService;
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
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
    public Optional<PendingTaskVM> partialUpdate(PendingTaskDTO pendingTaskDTO) {
        log.debug("Request to partially update PendingTask : {}", pendingTaskDTO);

        return pendingTaskRepository
            .findById(pendingTaskDTO.getId())
            .map(existingPendingTask -> {
                pendingTaskMapper.partialUpdate(existingPendingTask, pendingTaskDTO);

                return existingPendingTask;
            })
            .map(pendingTaskRepository::save)
            .map(pendingTaskMapper::toVm);
    }

    /**
     * Get all the pendingTasks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Page<PendingTaskVM> findAll(Pageable pageable) {
        log.debug("Request to get all PendingTasks");
        return pendingTaskRepository.findAll(pageable).map(pendingTaskMapper::toVm);
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

    public PendingTaskVM createNew(PendingTaskVM pendingTaskVM) {
        log.debug("Request to save PendingTask : {}", pendingTaskVM);
        PendingTaskDTO pendingTaskDTO = pendingTaskMapper.toDto(pendingTaskVM);

        BookDTO bookDTO = bookService.findById(pendingTaskVM.getBookId());
        pendingTaskDTO.setBook(bookDTO);
        pendingTaskDTO.setLibrarian(null);
        pendingTaskDTO.setStatus(PendingTaskStatus.PENDING);
        pendingTaskDTO.setVisitor(visitorService.getVisitorOfCurrentUser());
        pendingTaskDTO.setLibrary(libraryService.getSelectedLibrary());

        PendingTask pendingTask = pendingTaskMapper.toEntity(pendingTaskDTO);
        pendingTask = pendingTaskRepository.save(pendingTask);
        return pendingTaskMapper.toVm(pendingTask);
    }
}
