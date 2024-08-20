package com.xtramile.library2024.service;

import com.xtramile.library2024.domain.Library;
import com.xtramile.library2024.repository.LibraryRepository;
import com.xtramile.library2024.service.dto.LibrarianDTO;
import com.xtramile.library2024.service.dto.LibraryDTO;
import com.xtramile.library2024.service.mapper.LibraryMapper;
import com.xtramile.library2024.web.rest.vm.LibraryVM;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.xtramile.library2024.domain.Library}.
 */
@Service
@Transactional
public class LibraryService {

    private static final Logger log = LoggerFactory.getLogger(LibraryService.class);

    private final LibraryRepository libraryRepository;

    private final LibraryMapper libraryMapper;

    private final LibrarianService librarianService;

    public LibraryService(LibraryRepository libraryRepository, LibraryMapper libraryMapper, LibrarianService librarianService) {
        this.libraryRepository = libraryRepository;
        this.libraryMapper = libraryMapper;
        this.librarianService = librarianService;
    }

    /**
     * Save a library.
     *
     * @param libraryDTO the entity to save.
     * @return the persisted entity.
     */
    public LibraryDTO save(LibraryDTO libraryDTO) {
        log.debug("Request to save Library : {}", libraryDTO);
        Library library = libraryMapper.toEntity(libraryDTO);
        library = libraryRepository.save(library);
        return libraryMapper.toDto(library);
    }

    /**
     * Update a library.
     *
     * @param libraryDTO the entity to save.
     * @return the persisted entity.
     */
    public LibraryDTO update(LibraryDTO libraryDTO) {
        log.debug("Request to update Library : {}", libraryDTO);
        Library library = libraryMapper.toEntity(libraryDTO);
        library = libraryRepository.save(library);
        return libraryMapper.toDto(library);
    }

    /**
     * Partially update a library.
     *
     * @param libraryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LibraryDTO> partialUpdate(LibraryDTO libraryDTO) {
        log.debug("Request to partially update Library : {}", libraryDTO);

        return libraryRepository
            .findById(libraryDTO.getId())
            .map(existingLibrary -> {
                libraryMapper.partialUpdate(existingLibrary, libraryDTO);

                return existingLibrary;
            })
            .map(libraryRepository::save)
            .map(libraryMapper::toDto);
    }

    /**
     * Get all the libraries.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<LibraryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Libraries");
        return libraryRepository.findAll(pageable).map(libraryMapper::toDto);
    }

    /**
     * Get one library by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LibraryDTO> findOne(Long id) {
        log.debug("Request to get Library : {}", id);
        return libraryRepository.findById(id).map(libraryMapper::toDto);
    }

    /**
     * Delete the library by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Library : {}", id);
        libraryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<LibraryVM> getAllPublicLibraries() {
        log.debug("Request to get all public libraries");
        return libraryRepository
            .findAll()
            .stream()
            .<LibraryVM>map(library -> LibraryVM.builder().id(library.getId()).name(library.getName()).build())
            .collect(Collectors.toList());
    }

    public LibraryDTO getLibraryOfCurrentLibrarian() {
        log.debug("Request to get Library of current librarian");
        LibrarianDTO librarianDTO = librarianService.getLibrarianOfCurrentUser();
        return librarianDTO.getLibrary();
    }
}
