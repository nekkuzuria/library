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
        if (libraryDTO == null) {
            log.error("Attempted to save a null LibraryDTO");
            throw new IllegalArgumentException("LibraryDTO cannot be null");
        }

        log.debug("Request to save Library: {}", libraryDTO);

        try {
            Library library = libraryMapper.toEntity(libraryDTO);
            library = libraryRepository.save(library);
            LibraryDTO result = libraryMapper.toDto(library);

            log.debug("Successfully saved Library with ID: {}", library.getId());

            return result;
        } catch (Exception e) {
            log.error("Error occurred while saving LibraryDTO: {}", libraryDTO, e);
            throw new RuntimeException("Failed to save Library", e);
        }
    }

    /**
     * Update a library.
     *
     * @param libraryDTO the entity to save.
     * @return the persisted entity.
     */
    public LibraryDTO update(LibraryDTO libraryDTO) {
        log.debug("Request to update Library: {}", libraryDTO);

        if (libraryDTO == null) {
            log.error("Attempted to update with a null LibraryDTO");
            throw new IllegalArgumentException("LibraryDTO cannot be null");
        }

        try {
            Library library = libraryMapper.toEntity(libraryDTO);
            library = libraryRepository.save(library);
            return libraryMapper.toDto(library);
        } catch (Exception e) {
            log.error("Error occurred while updating LibraryDTO: {}", libraryDTO, e);
            throw new RuntimeException("Failed to update Library", e);
        }
    }

    /**
     * Partially update a library.
     *
     * @param libraryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LibraryDTO> partialUpdate(LibraryDTO libraryDTO) {
        log.debug("Request to partially update Library: {}", libraryDTO);

        if (libraryDTO == null) {
            log.error("Attempted to partially update with a null LibraryDTO");
            throw new IllegalArgumentException("LibraryDTO cannot be null");
        }

        return libraryRepository
            .findById(libraryDTO.getId())
            .map(existingLibrary -> {
                log.debug("Found existing Library with ID: {}", existingLibrary.getId());

                libraryMapper.partialUpdate(existingLibrary, libraryDTO);
                log.debug("Updated existing Library with new data: {}", existingLibrary);

                return libraryRepository.save(existingLibrary);
            })
            .map(library -> {
                log.debug("Saved updated Library with ID: {}", library.getId());
                return libraryMapper.toDto(library);
            })
            .or(() -> {
                log.warn("Library with ID {} not found for partial update", libraryDTO.getId());
                return Optional.empty();
            });
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
        log.debug("Request to get Selected Library");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Jwt jwt = (Jwt) principal;

        Long libraryId = jwt.getClaim("libraryId");
        log.debug("Request to get Library with ID: {}", libraryId);

        Library library = libraryRepository
            .findById(libraryId)
            .orElseThrow(() -> {
                log.error("Library with ID {} not found", libraryId);
                return new EntityNotFoundException("Library not found with ID " + libraryId);
            });

        log.debug("Successfully retrieved Library: {}", library);

        return libraryMapper.toDto(library);
    }

    public LibraryDTO getSelectedLibrary(String jwtToken) {
        log.debug("Request to get Library using JWT token");

        Jwt jwt = jwtDecoder.decode(jwtToken);
        Long libraryId = jwt.getClaim("libraryId");

        log.debug("Extracted Library ID from JWT: {}", libraryId);

        if (libraryId == 0) {
            log.warn("Invalid Library ID extracted from JWT: {}", libraryId);
            throw new IllegalArgumentException("Invalid Library ID extracted from JWT: " + libraryId);
        }

        return libraryRepository
            .findById(libraryId)
            .map(libraryMapper::toDto)
            .orElseThrow(() -> {
                log.error("Library with ID {} not found", libraryId);
                return new NoSuchElementException("Library with ID " + libraryId + " not found");
            });
    }

    public boolean isAdminLibraryValid(String jwt) {
        log.debug("Checking if the admin's library is valid using JWT");

        Long userId = securityUtils.getCurrentUserId(jwt);
        log.debug("Extracted User ID from JWT: {}", userId);

        LibraryDTO librarianLibrary = getLibraryOfCurrentLibrarian(userId);
        if (librarianLibrary == null) {
            log.debug("No library found for librarian with User ID: {}. Assuming user is a visitor.", userId);
            return true; // Means user is a visitor
        }

        LibraryDTO selectedLibrary = getSelectedLibrary(jwt);
        if (selectedLibrary == null) {
            log.error("Selected library is null for JWT: {}", jwt);
            return false;
        }

        boolean isValid = selectedLibrary.getId().equals(librarianLibrary.getId());
        log.debug(
            "Selected Library ID: {} matches Librarian's Library ID: {}: {}",
            selectedLibrary.getId(),
            librarianLibrary.getId(),
            isValid
        );

        return isValid;
    }
}
