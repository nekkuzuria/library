package com.xtramile.library2024.service.impl;

import com.xtramile.library2024.domain.Librarian;
import com.xtramile.library2024.domain.Library;
import com.xtramile.library2024.domain.Location;
import com.xtramile.library2024.domain.User;
import com.xtramile.library2024.repository.LibrarianRepository;
import com.xtramile.library2024.repository.UserRepository;
import com.xtramile.library2024.security.SecurityUtils;
import com.xtramile.library2024.service.LibrarianService;
import com.xtramile.library2024.service.dto.AdminUserDTO;
import com.xtramile.library2024.service.dto.LibrarianDTO;
import com.xtramile.library2024.service.dto.UserDTO;
import com.xtramile.library2024.service.mapper.LibrarianMapper;
import com.xtramile.library2024.service.mapper.LibraryMapper;
import com.xtramile.library2024.service.mapper.LocationMapper;
import com.xtramile.library2024.service.mapper.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import java.util.LinkedList;
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
 * Service Implementation for managing {@link Librarian}.
 */
@Service
@Transactional
public class LibrarianServiceImpl implements LibrarianService {

    private static final Logger log = LoggerFactory.getLogger(LibrarianServiceImpl.class);

    private final LibrarianRepository librarianRepository;

    private final LibrarianMapper librarianMapper;
    private final LibraryMapper libraryMapper;
    private final LocationMapper locationMapper;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public LibrarianServiceImpl(
        LibrarianRepository librarianRepository,
        LibrarianMapper librarianMapper,
        LibraryMapper libraryMapper,
        LocationMapper locationMapper,
        UserMapper userMapper,
        UserRepository userRepository
    ) {
        this.librarianRepository = librarianRepository;
        this.librarianMapper = librarianMapper;
        this.libraryMapper = libraryMapper;
        this.locationMapper = locationMapper;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    @Override
    public LibrarianDTO save(LibrarianDTO librarianDTO) {
        log.debug("Request to save Librarian : {}", librarianDTO);
        Librarian librarian = librarianMapper.toEntity(librarianDTO);
        librarian = librarianRepository.save(librarian);
        return librarianMapper.toDto(librarian);
    }

    @Override
    public LibrarianDTO update(LibrarianDTO librarianDTO) {
        log.debug("Request to update Librarian : {}", librarianDTO);
        Librarian librarian = librarianMapper.toEntity(librarianDTO);
        librarian = librarianRepository.save(librarian);
        return librarianMapper.toDto(librarian);
    }

    @Override
    public LibrarianDTO update(LibrarianDTO librarianDTO, User user, AdminUserDTO userDTO) {
        log.debug("Request to update Librarian with details: {}", librarianDTO);

        Long userId = user.getId();
        Librarian librarian = librarianRepository
            .findByUserId(userId)
            .orElseThrow(() -> {
                log.error("Librarian not found for User ID: {}", userId);
                return new RuntimeException("Librarian not found");
            });
        log.debug("Retrieved Librarian: {}", librarian);

        librarianDTO.setId(librarian.getId());
        librarianDTO.setLibrary(libraryMapper.toDto(librarian.getLibrary()));
        librarianDTO.setLocation(locationMapper.toDto(librarian.getLocation()));
        librarianDTO.setUser(userMapper.toDtoId(user));
        librarianDTO.setName(userDTO.getFirstName() + " " + userDTO.getLastName());

        librarian = librarianMapper.toEntity(librarianDTO);
        librarian = librarianRepository.save(librarian);

        log.info("Librarian updated successfully with ID: {}", librarian.getId());
        log.debug("Updated Librarian entity: {}", librarian);

        return librarianMapper.toDto(librarian);
    }

    @Override
    public Optional<LibrarianDTO> partialUpdate(LibrarianDTO librarianDTO) {
        log.debug("Request to partially update Librarian : {}", librarianDTO);

        return librarianRepository
            .findById(librarianDTO.getId())
            .map(existingLibrarian -> {
                librarianMapper.partialUpdate(existingLibrarian, librarianDTO);

                return existingLibrarian;
            })
            .map(librarianRepository::save)
            .map(librarianMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibrarianDTO> findAll() {
        log.debug("Request to get all Librarians");
        return librarianRepository.findAll().stream().map(librarianMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<LibrarianDTO> findAllWithEagerRelationships(Pageable pageable) {
        log.debug("Request to get all Librarians with eager relationships, Pageable: {}", pageable);

        Page<LibrarianDTO> result = librarianRepository.findAllWithEagerRelationships(pageable).map(librarianMapper::toDto);
        log.debug("Retrieved {} Librarians with eager relationships", result.getTotalElements());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LibrarianDTO> findOne(Long id) {
        log.debug("Request to get Librarian : {}", id);
        return librarianRepository.findOneWithEagerRelationships(id).map(librarianMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Librarian : {}", id);
        librarianRepository.deleteById(id);
    }

    @Override
    public LibrarianDTO getLibrarianOfCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Request to get Librarian for current User ID: {}", userId);

        return librarianRepository
            .findByUserId(userId)
            .map(librarian -> {
                log.debug("Found Librarian: {}", librarian);
                return librarianMapper.toDto(librarian);
            })
            .orElseGet(() -> {
                log.warn("No Librarian found for User ID: {}", userId);
                return null;
            });
    }

    @Override
    public LibrarianDTO getLibrarianByUserId(Long userId) {
        log.debug("Request to get Librarian for User ID: {}", userId);

        return librarianRepository
            .findByUserId(userId)
            .map(librarian -> {
                log.debug("Found Librarian: {}", librarian);
                return librarianMapper.toDto(librarian);
            })
            .orElseGet(() -> {
                log.warn("No Librarian found for User ID: {}", userId);
                return null;
            });
    }
}
