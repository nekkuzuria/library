package com.xtramile.library2024.service;

import com.xtramile.library2024.domain.PendingTask;
import com.xtramile.library2024.domain.VisitorBookStorage;
import com.xtramile.library2024.domain.enumeration.PendingTaskStatus;
import com.xtramile.library2024.domain.enumeration.PendingTaskType;
import com.xtramile.library2024.repository.BookRepository;
import com.xtramile.library2024.repository.PendingTaskRepository;
import com.xtramile.library2024.service.dto.*;
import com.xtramile.library2024.service.mapper.*;
import com.xtramile.library2024.web.rest.errors.PendingTaskAlreadyExistsException;
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
        log.debug("Book found: {}", bookDTO);

        pendingTaskDTO.setBook(bookDTO);
        pendingTaskDTO.setLibrarian(null);
        pendingTaskDTO.setStatus(PendingTaskStatus.PENDING);
        pendingTaskDTO.setVisitor(visitorService.getVisitorOfCurrentUser());
        pendingTaskDTO.setLibrary(libraryService.getSelectedLibrary());

        if (pendingTaskVM.getVisitorBookStorageId() != null) {
            Optional<VisitorBookStorageDTO> vbs = visitorBookStorageService.findOne(pendingTaskVM.getVisitorBookStorageId());
            if (vbs.isPresent()) {
                log.debug("VisitorBookStorage found: {}", vbs.get());

                if (
                    pendingTaskVM.getType() == PendingTaskType.RETURN &&
                    pendingTaskRepository
                        .findByVisitorBookStorageAndTypeAndStatus(
                            visitorBookStorageMapper.toEntity(vbs.get()),
                            PendingTaskType.RETURN,
                            PendingTaskStatus.PENDING
                        )
                        .isPresent()
                ) {
                    log.warn(
                        "A pending return request already exists for VisitorBookStorage ID: {}",
                        pendingTaskVM.getVisitorBookStorageId()
                    );
                    throw new PendingTaskAlreadyExistsException("A request to return this book already exists.");
                }

                pendingTaskDTO.setVisitorBookStorage(vbs.get());
            } else {
                log.debug("No VisitorBookStorage found for ID: {}", pendingTaskVM.getVisitorBookStorageId());
                pendingTaskDTO.setVisitorBookStorage(null);
            }
        } else {
            log.debug("No VisitorBookStorage ID provided");
            pendingTaskDTO.setVisitorBookStorage(null);
        }

        PendingTask pendingTask = pendingTaskMapper.toEntity(pendingTaskDTO);
        log.debug("Saving PendingTask : {}", pendingTask);
        pendingTask = pendingTaskRepository.save(pendingTask);
        log.debug("Saved PendingTask with ID: {}", pendingTask.getId());

        return pendingTaskMapper.toVm(pendingTask);
    }

    public Optional<PendingTaskVM> processPendingTask(PendingTaskDTO pendingTaskDTO) {
        log.debug("Request to process PendingTask : {}", pendingTaskDTO);

        Optional<PendingTaskVM> pendingTaskVM = pendingTaskRepository
            .findById(pendingTaskDTO.getId())
            .map(existingPendingTask -> {
                log.debug("Found existing PendingTask : {}", existingPendingTask);

                PendingTaskType type = existingPendingTask.getType();
                PendingTaskStatus status = pendingTaskDTO.getStatus();
                log.debug("Processing PendingTask with type: {} and status: {}", type, status);

                LibrarianDTO currentLibrarian = librarianService.getLibrarianOfCurrentUser();
                log.debug("Current Librarian: {}", currentLibrarian);

                if (status == PendingTaskStatus.APPROVED) {
                    log.debug("Processing approval for PendingTask");
                    VisitorBookStorageDTO newVbs = handleApproval(type, currentLibrarian, existingPendingTask);
                    pendingTaskDTO.setVisitorBookStorage(newVbs);
                    log.debug("Updated VisitorBookStorage: {}", newVbs);
                }

                pendingTaskDTO.setLibrarian(currentLibrarian);

                pendingTaskMapper.partialUpdate(existingPendingTask, pendingTaskDTO);
                log.debug("PendingTask updated with new data: {}", existingPendingTask);

                return existingPendingTask;
            })
            .map(pendingTaskRepository::save)
            .map(pendingTaskMapper::toVm);

        log.debug("Processed PendingTaskVM: {}", pendingTaskVM);
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
        log.debug("Handling approval for PendingTaskType: {} with existingPendingTask: {}", type, existingPendingTask);

        VisitorBookStorageDTO savedVbs = null;
        VisitorDTO visitorDTO = visitorMapper.toDto(existingPendingTask.getVisitor());
        BookDTO bookDTO = bookMapper.toDto(existingPendingTask.getBook());
        BookStorageDTO bookStorageDTO = bookStorageMapper.toDto(existingPendingTask.getBook().getBookStorage());
        Integer quantity = existingPendingTask.getQuantity();

        log.debug("VisitorDTO: {}", visitorDTO);
        log.debug("BookDTO: {}", bookDTO);
        log.debug("BookStorageDTO before operation: {}", bookStorageDTO);
        log.debug("Quantity: {}", quantity);

        if (type == PendingTaskType.BORROW) {
            log.debug("Processing BORROW type");
            VisitorBookStorageDTO vbsDTO = new VisitorBookStorageDTO();
            vbsDTO.setBorrowDate(existingPendingTask.getCreatedDate().atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDate());
            vbsDTO.setVisitor(visitorDTO);
            vbsDTO.setBook(bookDTO);
            vbsDTO.setQuantity(quantity);
            savedVbs = visitorBookStorageService.save(vbsDTO);

            bookStorageDTO.setQuantity(bookStorageDTO.getQuantity() - quantity);
            bookStorageDTO = bookStorageService.save(bookStorageDTO);
            log.debug("BookStorageDTO after BORROW operation: {}", bookStorageDTO);
        } else if (type == PendingTaskType.RETURN) {
            log.debug("Processing RETURN type");
            VisitorBookStorage vbs = existingPendingTask.getVisitorBookStorage();
            vbs.setReturnDate(LocalDate.now());
            savedVbs = visitorBookStorageService.save(visitorBookStorageMapper.toDto(vbs));

            bookStorageDTO.setQuantity(bookStorageDTO.getQuantity() + quantity);
            bookStorageDTO = bookStorageService.save(bookStorageDTO);
            log.debug("BookStorageDTO after RETURN operation: {}", bookStorageDTO);
        }

        VisitDTO visitDTO = new VisitDTO();
        visitDTO.setDate(LocalDate.now());
        visitDTO.setLibrary(libraryMapper.toDto(existingPendingTask.getLibrary()));
        visitDTO.setLibrarian(currentLibrarian);
        visitDTO.setVisitor(visitorDTO);
        visitDTO.setVisitorBookStorage(savedVbs);
        visitService.save(visitDTO);

        log.debug("Created VisitDTO: {}", visitDTO);
        log.debug("Saved VisitorBookStorageDTO: {}", savedVbs);

        return savedVbs;
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Page<PendingTaskVM> findAllFromCurrentLibrary(Pageable pageable) {
        log.debug("Request to get all PendingTasks");
        LibraryDTO libraryDTO = libraryService.getLibraryOfCurrentLibrarian();
        return pendingTaskRepository.findByLibrary(libraryMapper.toEntity(libraryDTO), pageable).map(pendingTaskMapper::toVm);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Page<PendingTaskVM> findAllFromCurrentVisitor(Pageable pageable) {
        log.debug("Request to get all PendingTasks of visitor");
        VisitorDTO visitorDTO = visitorService.getVisitorOfCurrentUser();
        return pendingTaskRepository.findByVisitor(visitorMapper.toEntity(visitorDTO), pageable).map(pendingTaskMapper::toVm);
    }
}
