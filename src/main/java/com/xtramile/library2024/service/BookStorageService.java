package com.xtramile.library2024.service;

import com.xtramile.library2024.domain.BookStorage;
import com.xtramile.library2024.repository.BookStorageRepository;
import com.xtramile.library2024.service.dto.BookStorageDTO;
import com.xtramile.library2024.service.dto.LibraryDTO;
import com.xtramile.library2024.service.mapper.BookStorageMapper;
import com.xtramile.library2024.service.mapper.LibraryMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.xtramile.library2024.domain.BookStorage}.
 */
@Service
@Transactional
public class BookStorageService {

    private static final Logger log = LoggerFactory.getLogger(BookStorageService.class);

    private final BookStorageRepository bookStorageRepository;

    private final BookStorageMapper bookStorageMapper;

    private final LibraryService libraryService;

    private final LibraryMapper libraryMapper;

    public BookStorageService(
        BookStorageRepository bookStorageRepository,
        BookStorageMapper bookStorageMapper,
        LibraryService libraryService,
        LibraryMapper libraryMapper
    ) {
        this.bookStorageRepository = bookStorageRepository;
        this.bookStorageMapper = bookStorageMapper;
        this.libraryService = libraryService;
        this.libraryMapper = libraryMapper;
    }

    /**
     * Save a bookStorage.
     *
     * @param bookStorageDTO the entity to save.
     * @return the persisted entity.
     */
    public BookStorageDTO save(BookStorageDTO bookStorageDTO) {
        log.debug("Request to save BookStorage : {}", bookStorageDTO);
        bookStorageDTO.setLibrary(libraryService.getLibraryOfCurrentUser());
        BookStorage bookStorage = bookStorageMapper.toEntity(bookStorageDTO);
        bookStorage = bookStorageRepository.save(bookStorage);
        return bookStorageMapper.toDto(bookStorage);
    }

    /**
     * Update a bookStorage.
     *
     * @param bookStorageDTO the entity to save.
     * @return the persisted entity.
     */
    public BookStorageDTO update(BookStorageDTO bookStorageDTO) {
        log.debug("Request to update BookStorage : {}", bookStorageDTO);
        BookStorage bookStorage = bookStorageMapper.toEntity(bookStorageDTO);
        bookStorage = bookStorageRepository.save(bookStorage);
        return bookStorageMapper.toDto(bookStorage);
    }

    /**
     * Partially update a bookStorage.
     *
     * @param bookStorageDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BookStorageDTO> partialUpdate(BookStorageDTO bookStorageDTO) {
        log.debug("Request to partially update BookStorage : {}", bookStorageDTO);

        return bookStorageRepository
            .findById(bookStorageDTO.getId())
            .map(existingBookStorage -> {
                bookStorageMapper.partialUpdate(existingBookStorage, bookStorageDTO);

                return existingBookStorage;
            })
            .map(bookStorageRepository::save)
            .map(bookStorageMapper::toDto);
    }

    /**
     * Get all the bookStorages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<BookStorageDTO> findAll(Pageable pageable) {
        log.debug("Request to get all BookStorages");
        return bookStorageRepository.findAll(pageable).map(bookStorageMapper::toDto);
    }

    /**
     * Get one bookStorage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BookStorageDTO> findOne(Long id) {
        log.debug("Request to get BookStorage : {}", id);
        return bookStorageRepository.findById(id).map(bookStorageMapper::toDto);
    }

    /**
     * Delete the bookStorage by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete BookStorage : {}", id);
        bookStorageRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<BookStorage> findAllByLibrary(LibraryDTO libraryDTO) {
        log.debug("Request to get all BookStorage of library: {}", libraryDTO);
        return bookStorageRepository.findByLibrary(libraryMapper.toEntity(libraryDTO));
    }
}
