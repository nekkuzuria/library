package com.xtramile.library2024.service;

import com.xtramile.library2024.domain.Book;
import com.xtramile.library2024.domain.BookStorage;
import com.xtramile.library2024.domain.File;
import com.xtramile.library2024.domain.VisitorBookStorage;
import com.xtramile.library2024.repository.BookRepository;
import com.xtramile.library2024.repository.VisitorBookStorageRepository;
import com.xtramile.library2024.service.dto.BookDTO;
import com.xtramile.library2024.service.dto.BookStorageDTO;
import com.xtramile.library2024.service.dto.LibraryDTO;
import com.xtramile.library2024.service.mapper.BookMapper;
import com.xtramile.library2024.service.mapper.BookStorageMapper;
import com.xtramile.library2024.service.mapper.LibraryMapper;
import com.xtramile.library2024.web.rest.vm.BookVM;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.xtramile.library2024.domain.Book}.
 */
@Service
@Transactional
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    private final LibraryService libraryService;

    private final BookStorageService bookStorageService;

    private final BookStorageMapper bookStorageMapper;
    private final VisitorBookStorageRepository visitorBookStorageRepository;

    public BookService(
        BookRepository bookRepository,
        BookMapper bookMapper,
        LibraryService libraryService,
        BookStorageService bookStorageService,
        BookStorageMapper bookStorageMapper,
        FileService fileService,
        VisitorBookStorageRepository visitorBookStorageRepository
    ) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.libraryService = libraryService;
        this.bookStorageService = bookStorageService;
        this.bookStorageMapper = bookStorageMapper;
        this.visitorBookStorageRepository = visitorBookStorageRepository;
    }

    /**
     * Save a book.
     *
     * @param bookDTO the entity to save.
     * @return the persisted entity.
     */
    public BookDTO save(BookDTO bookDTO) {
        log.debug("Request to save Book : {}", bookDTO);
        Book book = bookMapper.toEntity(bookDTO);
        book = bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    /**
     * Update a book.
     *
     * @param bookVM the entity to save.
     * @return the persisted entity.
     */
    public BookDTO update(BookVM bookVM) {
        log.debug("Request to update Book : {}", bookVM);
        Book book = bookMapper.toEntity(bookVM);

        bookStorageService.partialUpdate(bookStorageMapper.toDto(book.getBookStorage()));
        book = bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    /**
     * Partially update a book.
     *
     * @param bookVM the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BookDTO> partialUpdate(BookVM bookVM) {
        log.debug("Request to partially update Book : {}", bookVM);

        return bookRepository
            .findById(bookVM.getId())
            .map(existingBook -> {
                bookMapper.partialUpdate(existingBook, bookMapper.toDTO(bookVM));
                bookStorageService.partialUpdate(bookStorageMapper.toDto(bookMapper.toEntity(bookVM).getBookStorage()));
                return existingBook;
            })
            .map(bookRepository::save)
            .map(bookMapper::toDto);
    }

    /**
     * Get all the books.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<BookDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Books");
        return bookRepository.findAll(pageable).map(bookMapper::toDto);
    }

    /**
     *  Get all the books where VisitorBookStorage is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<BookDTO> findAllWhereVisitorBookStorageIsNull() {
        log.debug("Request to get all books where VisitorBookStorage is null");
        return StreamSupport.stream(bookRepository.findAll().spliterator(), false)
            .map(bookMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one book by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BookVM> findOne(Long id) {
        log.debug("Request to get Book : {}", id);
        return bookRepository.findById(id).map(bookMapper::toVM);
    }

    /**
     * Delete the book by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Book : {}", id);
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            Long bookStorageId = book.getBookStorage().getId();
            if (bookStorageId != null) {
                bookStorageService.delete(bookStorageId);
            } else {
                log.warn("BookStorage for book id {} not found", book.getId());
            }
            bookRepository.deleteById(id);
        } else {
            log.warn("Book with id {} not found", id);
            throw new EntityNotFoundException("Book with id " + id + " not found");
        }
    }

    @Transactional(readOnly = true)
    public Page<BookVM> getBooksForCurrentUserLibrary(Pageable pageable) {
        log.debug("Getting current user's library...");
        LibraryDTO libraryDTO = libraryService.getSelectedLibrary();
        if (libraryDTO == null) {
            log.error("Library not found for the current user");
            throw new EntityNotFoundException("Library not found for current user");
        }

        log.debug("Fetching book storages for the library...");
        List<BookStorage> bookStorages = bookStorageService.findAllByLibrary(libraryDTO);
        if (bookStorages.isEmpty()) {
            log.error("No book storages found for the library");
            throw new EntityNotFoundException("No book storages found for the library");
        }

        List<Long> bookStorageIds = bookStorages.stream().map(BookStorage::getId).collect(Collectors.toList());

        log.debug("Fetching books for the book storages...");
        Page<BookVM> result = bookRepository.findByBookStorageIds(bookStorageIds, pageable).map(bookMapper::toVM);

        log.debug("Returning the result...");
        return result;
    }

    @Transactional
    public BookVM createBook(BookVM bookVm) throws IOException {
        log.debug("Request to create Book with details: {}", bookVm);

        BookStorageDTO bookStorage = new BookStorageDTO();
        bookStorage.setQuantity(bookVm.getQuantity());
        log.debug("BookStorageDTO created with quantity: {}", bookVm.getQuantity());

        BookStorageDTO savedBookStorage = bookStorageService.save(bookStorage);
        log.debug("BookStorage saved with id: {}", savedBookStorage.getId());

        bookVm.setBookStorageId(savedBookStorage.getId());
        Book book = bookMapper.toEntity(bookVm);
        Book savedBook = bookRepository.save(book);
        log.debug("Book saved with id: {}", savedBook.getId());

        return bookMapper.toVM(savedBook);
    }

    public BookDTO findById(Long id) {
        log.debug("Request to find Book by id: {}", id);

        Book book = bookRepository
            .findById(id)
            .orElseThrow(() -> {
                log.warn("Book not found with id: {}", id);
                return new EntityNotFoundException("Book not found");
            });

        log.debug("Book found with id: {}", id);
        return bookMapper.toDto(book);
    }

    public void updateBookImage(Long bookId, File file) {
        log.debug("Request to update image for Book with id: {}", bookId);

        Book book = bookRepository
            .findById(bookId)
            .orElseThrow(() -> {
                log.warn("Book not found with id: {}", bookId);
                return new RuntimeException("Book not found");
            });

        book.setFile(file);
        bookRepository.save(book);
        log.info("Book updated with new image file: {}", file.getId());
    }

    public boolean isBookBorrowed(Long bookId) {
        log.debug("Checking if Book with id {} is borrowed", bookId);
        boolean isBorrowed = visitorBookStorageRepository.existsByBookIdAndReturnDateIsNull(bookId);

        log.debug("Book with id {} is borrowed: {}", bookId, isBorrowed);
        return isBorrowed;
    }
}
