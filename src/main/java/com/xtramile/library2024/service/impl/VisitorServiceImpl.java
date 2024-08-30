package com.xtramile.library2024.service.impl;

import com.xtramile.library2024.domain.*;
import com.xtramile.library2024.repository.VisitorRepository;
import com.xtramile.library2024.security.SecurityUtils;
import com.xtramile.library2024.service.VisitorService;
import com.xtramile.library2024.service.dto.AdminUserDTO;
import com.xtramile.library2024.service.dto.LibrarianDTO;
import com.xtramile.library2024.service.dto.VisitorDTO;
import com.xtramile.library2024.service.mapper.LibraryMapper;
import com.xtramile.library2024.service.mapper.LocationMapper;
import com.xtramile.library2024.service.mapper.UserMapper;
import com.xtramile.library2024.service.mapper.VisitorMapper;
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
 * Service Implementation for managing {@link com.xtramile.library2024.domain.Visitor}.
 */
@Service
@Transactional
public class VisitorServiceImpl implements VisitorService {

    private static final Logger log = LoggerFactory.getLogger(VisitorServiceImpl.class);

    private final VisitorRepository visitorRepository;

    private final VisitorMapper visitorMapper;
    private final LibraryMapper libraryMapper;
    private final LocationMapper locationMapper;
    private final UserMapper userMapper;

    public VisitorServiceImpl(
        VisitorRepository visitorRepository,
        VisitorMapper visitorMapper,
        LibraryMapper libraryMapper,
        LocationMapper locationMapper,
        UserMapper userMapper
    ) {
        this.visitorRepository = visitorRepository;
        this.visitorMapper = visitorMapper;
        this.libraryMapper = libraryMapper;
        this.locationMapper = locationMapper;
        this.userMapper = userMapper;
    }

    @Override
    public VisitorDTO save(VisitorDTO visitorDTO) {
        log.debug("Request to save Visitor : {}", visitorDTO);
        Visitor visitor = visitorMapper.toEntity(visitorDTO);
        visitor = visitorRepository.save(visitor);
        return visitorMapper.toDto(visitor);
    }

    @Override
    public VisitorDTO update(VisitorDTO visitorDTO) {
        log.debug("Request to update Visitor : {}", visitorDTO);
        Visitor visitor = visitorMapper.toEntity(visitorDTO);
        visitor = visitorRepository.save(visitor);
        return visitorMapper.toDto(visitor);
    }

    @Override
    public VisitorDTO update(VisitorDTO visitorDTO, User user, AdminUserDTO userDTO) {
        log.debug("Request to update Visitor with DTO: {}", visitorDTO);

        Long userId = user.getId();

        // Retrieve the existing Visitor entity
        Visitor existingVisitor = visitorRepository
            .findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Visitor not found for user ID: " + userId));
        log.debug("Retrieved existing Visitor: {}", existingVisitor);

        Library library = existingVisitor.getLibrary();
        Location location = existingVisitor.getAddress();
        log.debug("Retrieved Library: {}", library);
        log.debug("Retrieved Location: {}", location);

        Long id = existingVisitor.getId();
        visitorDTO.setId(id);
        visitorDTO.setLibrary(libraryMapper.toDto(library));
        visitorDTO.setAddress(locationMapper.toDto(location));
        visitorDTO.setUser(userMapper.toDtoId(user));
        visitorDTO.setName(userDTO.getFirstName() + " " + userDTO.getLastName());

        Visitor updatedVisitor = visitorMapper.toEntity(visitorDTO);
        log.debug("Converted DTO to entity: {}", updatedVisitor);

        updatedVisitor = visitorRepository.save(updatedVisitor);
        log.debug("Saved updated Visitor: {}", updatedVisitor);

        VisitorDTO resultDto = visitorMapper.toDto(updatedVisitor);
        log.debug("Returning updated VisitorDTO: {}", resultDto);

        return resultDto;
    }

    @Override
    public Optional<VisitorDTO> partialUpdate(VisitorDTO visitorDTO) {
        log.debug("Request to partially update Visitor : {}", visitorDTO);

        return visitorRepository
            .findById(visitorDTO.getId())
            .map(existingVisitor -> {
                visitorMapper.partialUpdate(existingVisitor, visitorDTO);

                return existingVisitor;
            })
            .map(visitorRepository::save)
            .map(visitorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitorDTO> findAll() {
        log.debug("Request to get all Visitors");
        return visitorRepository.findAll().stream().map(visitorMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public Page<VisitorDTO> findAllWithEagerRelationships(Pageable pageable) {
        log.debug("Request to get all Visitors with eager relationships. Pageable: {}", pageable);

        Page<Visitor> visitorsPage = visitorRepository.findAllWithEagerRelationships(pageable);
        log.debug("Fetched {} Visitors from repository", visitorsPage.getTotalElements());

        Page<VisitorDTO> visitorDTOPage = visitorsPage.map(visitorMapper::toDto);
        log.debug("Mapped Visitors to DTOs. Total DTOs: {}", visitorDTOPage.getTotalElements());

        return visitorDTOPage;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VisitorDTO> findOne(Long id) {
        log.debug("Request to get Visitor : {}", id);
        return visitorRepository.findOneWithEagerRelationships(id).map(visitorMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Visitor : {}", id);
        visitorRepository.deleteById(id);
    }

    @Override
    public Long getVisitorIdOfCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching Visitor ID for current user with ID: {}", userId);

        Visitor visitor = visitorRepository
            .findByUserId(userId)
            .orElseThrow(() -> {
                log.error("Visitor not found for user with ID: {}", userId);
                return new EntityNotFoundException("Visitor not found for user");
            });

        Long visitorId = visitor.getId();
        log.debug("Found Visitor ID: {} for user with ID: {}", visitorId, userId);

        return visitorId;
    }

    @Override
    public VisitorDTO getVisitorOfCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.debug("Fetching Visitor for current user with ID: {}", userId);

        return visitorRepository
            .findByUserId(userId)
            .map(visitor -> {
                log.debug("Found Visitor with ID: {} for user with ID: {}", visitor.getId(), userId);
                return visitorMapper.toDto(visitor);
            })
            .orElseGet(() -> {
                log.warn("No Visitor found for user with ID: {}", userId);
                return null;
            });
    }
}
