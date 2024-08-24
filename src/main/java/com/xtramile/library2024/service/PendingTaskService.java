package com.xtramile.library2024.service;

import com.xtramile.library2024.domain.PendingTask;
import com.xtramile.library2024.domain.VisitorBookStorage;
import com.xtramile.library2024.domain.enumeration.PendingTaskStatus;
import com.xtramile.library2024.domain.enumeration.PendingTaskType;
import com.xtramile.library2024.repository.BookRepository;
import com.xtramile.library2024.repository.PendingTaskRepository;
import com.xtramile.library2024.service.dto.*;
import com.xtramile.library2024.service.mapper.*;
import com.xtramile.library2024.web.rest.vm.PendingTaskVM;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
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

    private final LibraryService libraryService;

    private final VisitorService visitorService;

    private final BookService bookService;

    private final VisitorBookStorageService visitorBookStorageService;

    private final LibrarianService librarianService;

    private final VisitService visitService;

    private final BookStorageService bookStorageService;

    private final VisitorMapper visitorMapper;

    private final BookMapper bookMapper;

    private final VisitorBookStorageMapper visitorBookStorageMapper;

    private final BookStorageMapper bookStorageMapper;

    private final BookRepository bookRepository;

    public PendingTaskService(
        PendingTaskRepository pendingTaskRepository,
        PendingTaskMapper pendingTaskMapper,
        LibraryMapper libraryMapper,
        LibraryService libraryService,
        VisitorService visitorService,
        BookService bookService,
        BookRepository bookRepository,
        BookMapper bookMapper,
        VisitorBookStorageService visitorBookStorageService,
        VisitorMapper visitorMapper,
        VisitorBookStorageMapper visitorBookStorageMapper,
        LibrarianService librarianService,
        VisitService visitService,
        BookStorageService bookStorageService,
        BookStorageMapper bookStorageMapper
    ) {
        this.pendingTaskRepository = pendingTaskRepository;
        this.pendingTaskMapper = pendingTaskMapper;
        this.libraryMapper = libraryMapper;
        this.libraryService = libraryService;
        this.visitorService = visitorService;
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.visitorBookStorageService = visitorBookStorageService;
        this.visitorMapper = visitorMapper;
        this.visitorBookStorageMapper = visitorBookStorageMapper;
        this.librarianService = librarianService;
        this.visitService = visitService;
        this.bookStorageService = bookStorageService;
        this.bookStorageMapper = bookStorageMapper;
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

    public Optional<PendingTaskVM> processPendingTask(PendingTaskDTO pendingTaskDTO) {
        log.debug("Request to partially update PendingTask : {}", pendingTaskDTO);

        Optional<PendingTaskVM> pendingTaskVM = pendingTaskRepository
            .findById(pendingTaskDTO.getId())
            .map(existingPendingTask -> {
                PendingTaskType type = existingPendingTask.getType();
                PendingTaskStatus status = pendingTaskDTO.getStatus();

                LibrarianDTO currentLibrarian = librarianService.getLibrarianOfCurrentUser();

                if (status == PendingTaskStatus.APPROVED) {
                    VisitorBookStorageDTO newVbs = handleApproval(type, currentLibrarian, existingPendingTask);
                    pendingTaskDTO.setVisitorBookStorage(newVbs);
                }

                pendingTaskDTO.setLibrarian(currentLibrarian);

                pendingTaskMapper.partialUpdate(existingPendingTask, pendingTaskDTO);
                return existingPendingTask;
            })
            .map(pendingTaskRepository::save)
            .map(pendingTaskMapper::toVm);

        return pendingTaskVM;
    }

    /**
     * Handles the approval process for a pending task.
     *
     * This method is responsible for processing the approval of a pending task.
     * It creates a new VisitorBookStorage and Visit, and
     * it alters the quantity in BookStorage depending on if book is being returned or is being borrowed
     *
     * @param type The type of the pending task that needs approval.
     * @param currentLibrarian The current librarian who is performing the approval action.
     * @param existingPendingTask The pending task that is being approved.
     * @return VisitorBookStorageDTO The visitor book storage details after approval processing.
     */
    private VisitorBookStorageDTO handleApproval(PendingTaskType type, LibrarianDTO currentLibrarian, PendingTask existingPendingTask) {
        VisitorBookStorageDTO savedVbs = null;
        VisitorDTO visitorDTO = visitorMapper.toDto(existingPendingTask.getVisitor());
        BookDTO bookDTO = bookMapper.toDto(existingPendingTask.getBook());
        BookStorageDTO bookStorageDTO = bookStorageMapper.toDto(existingPendingTask.getBook().getBookStorage());
        Integer quantity = existingPendingTask.getQuantity();

        if (type == PendingTaskType.BORROW) {
            VisitorBookStorageDTO vbsDTO = new VisitorBookStorageDTO();
            vbsDTO.setBorrowDate(existingPendingTask.getCreatedDate().atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDate());
            vbsDTO.setVisitor(visitorDTO);
            vbsDTO.setBook(bookDTO);
            vbsDTO.setQuantity(quantity);
            savedVbs = visitorBookStorageService.save(vbsDTO);

            bookStorageDTO.setQuantity(bookStorageDTO.getQuantity() - quantity);
            bookStorageService.save(bookStorageDTO);
        } else if (type == PendingTaskType.RETURN) {
            VisitorBookStorage vbs = existingPendingTask.getVisitorBookStorage();
            vbs.setReturnDate(LocalDate.now());
            savedVbs = visitorBookStorageService.save(visitorBookStorageMapper.toDto(vbs));

            bookStorageDTO.setQuantity(bookStorageDTO.getQuantity() + quantity);
            bookStorageService.save(bookStorageDTO);
        }
        VisitDTO visitDTO = new VisitDTO();
        visitDTO.setDate(LocalDate.now());
        visitDTO.setLibrary(libraryMapper.toDto(existingPendingTask.getLibrary()));
        visitDTO.setLibrarian(currentLibrarian);
        visitDTO.setVisitor(visitorDTO);
        visitDTO.setVisitorBookStorage(savedVbs);
        visitService.save(visitDTO);

        return savedVbs;
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Page<PendingTaskVM> findAllFromCurrentLibrary(Pageable pageable) {
        log.debug("Request to get all PendingTasks");
        LibraryDTO libraryDTO = libraryService.getLibraryOfCurrentLibrarian();
        return pendingTaskRepository.findByLibrary(libraryMapper.toEntity(libraryDTO), pageable).map(pendingTaskMapper::toVm);
    }
}
