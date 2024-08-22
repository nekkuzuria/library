package com.xtramile.library2024.service.impl;

import com.xtramile.library2024.domain.Librarian;
import com.xtramile.library2024.repository.LibrarianRepository;
import com.xtramile.library2024.security.SecurityUtils;
import com.xtramile.library2024.service.LibrarianService;
import com.xtramile.library2024.service.dto.LibrarianDTO;
import com.xtramile.library2024.service.mapper.LibrarianMapper;
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
 * Service Implementation for managing {@link com.xtramile.library2024.domain.Librarian}.
 */
@Service
@Transactional
public class LibrarianServiceImpl implements LibrarianService {

    private static final Logger log = LoggerFactory.getLogger(LibrarianServiceImpl.class);

    private final LibrarianRepository librarianRepository;

    private final LibrarianMapper librarianMapper;

    public LibrarianServiceImpl(LibrarianRepository librarianRepository, LibrarianMapper librarianMapper) {
        this.librarianRepository = librarianRepository;
        this.librarianMapper = librarianMapper;
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
        return librarianRepository.findAllWithEagerRelationships(pageable).map(librarianMapper::toDto);
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
        return librarianRepository.findByUserId(userId).map(librarianMapper::toDto).orElse(null);
    }
}
