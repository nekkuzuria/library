package com.xtramile.library2024.service;

import com.xtramile.library2024.domain.Library;
import com.xtramile.library2024.domain.User;
import com.xtramile.library2024.repository.LibraryRepository;
import com.xtramile.library2024.repository.UserRepository;
import com.xtramile.library2024.security.SecurityUtils;
import com.xtramile.library2024.service.dto.LibrarianDTO;
import com.xtramile.library2024.service.dto.LibraryDTO;
import com.xtramile.library2024.service.dto.VisitorDTO;
import com.xtramile.library2024.service.mapper.LibraryMapper;
import com.xtramile.library2024.web.rest.vm.LibraryVM;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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

    private final VisitorService visitorService;

    private final JwtDecoder jwtDecoder;
    private final SecurityUtils securityUtils;

    public LibraryService(
        LibraryRepository libraryRepository,
        LibraryMapper libraryMapper,
        LibrarianService librarianService,
        VisitorService visitorService,
        JwtDecoder jwtDecoder,
        SecurityUtils securityUtils
    ) {
        this.libraryRepository = libraryRepository;
        this.libraryMapper = libraryMapper;
        this.librarianService = librarianService;
        this.visitorService = visitorService;
        this.jwtDecoder = jwtDecoder;
        this.securityUtils = securityUtils;
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

    public LibraryDTO getLibraryOfCurrentUser() {
        log.debug("Request to get Library of current user");

        LibrarianDTO librarianDTO = librarianService.getLibrarianOfCurrentUser();
        if (librarianDTO != null) {
            return librarianDTO.getLibrary();
        }

        VisitorDTO visitorDTO = visitorService.getVisitorOfCurrentUser();
        if (visitorDTO != null) {
            return visitorDTO.getLibrary();
        }

        log.warn("User is neither a librarian nor a visitor");
        throw new EntityNotFoundException("User is neither a librarian nor a visitor");
    }

    public LibraryDTO getLibraryOfCurrentLibrarian(Long userId) {
        log.debug("Request to get Library of current user");

        LibrarianDTO librarianDTO = librarianService.getLibrarianByUserId(userId);
        if (librarianDTO != null) {
            return librarianDTO.getLibrary();
        }
        return null;
    }

    public LibraryDTO getSelectedLibrary() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Jwt jwt = (Jwt) principal;
        return libraryMapper.toDto(libraryRepository.findById(jwt.getClaim("libraryId")).get());
    }

    public LibraryDTO getSelectedLibrary(String jwtToken) {
        Jwt jwt = jwtDecoder.decode(jwtToken);
        Long libraryId = jwt.getClaim("libraryId");
        if (libraryId == 0) {
            return null;
        }
        return libraryRepository
            .findById(libraryId)
            .map(libraryMapper::toDto)
            .orElseThrow(() -> new NoSuchElementException("Library with id " + libraryId + " not found"));
    }

    public boolean isAdminLibraryValid(String jwt) {
        Long userId = securityUtils.getCurrentUserId(jwt);
        LibraryDTO librarianLibrary = getLibraryOfCurrentLibrarian(userId);
        if (librarianLibrary == null) {
            return true; // Means user is visitor
        }
        return getSelectedLibrary(jwt).getId().equals(librarianLibrary.getId());
    }
}
