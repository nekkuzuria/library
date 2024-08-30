package com.xtramile.library2024.service;

import com.xtramile.library2024.domain.Librarian;
import com.xtramile.library2024.domain.Location;
import com.xtramile.library2024.domain.User;
import com.xtramile.library2024.domain.Visitor;
import com.xtramile.library2024.repository.LibrarianRepository;
import com.xtramile.library2024.repository.LocationRepository;
import com.xtramile.library2024.repository.VisitorRepository;
import com.xtramile.library2024.service.dto.AdminUserDTO;
import com.xtramile.library2024.service.dto.LocationDTO;
import com.xtramile.library2024.service.mapper.LocationMapper;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.xtramile.library2024.domain.Location}.
 */
@Service
@Transactional
public class LocationService {

    private static final Logger log = LoggerFactory.getLogger(LocationService.class);

    private final LocationRepository locationRepository;

    private final LocationMapper locationMapper;
    private final LibrarianRepository librarianRepository;
    private final VisitorRepository visitorRepository;

    public LocationService(
        LocationRepository locationRepository,
        LocationMapper locationMapper,
        LibrarianRepository librarianRepository,
        VisitorRepository visitorRepository
    ) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
        this.librarianRepository = librarianRepository;
        this.visitorRepository = visitorRepository;
    }

    /**
     * Save a location.
     *
     * @param locationDTO the entity to save.
     * @return the persisted entity.
     */
    public LocationDTO save(LocationDTO locationDTO) {
        log.debug("Request to save Location : {}", locationDTO);
        Location location = locationMapper.toEntity(locationDTO);
        location = locationRepository.save(location);
        return locationMapper.toDto(location);
    }

    /**
     * Update a location.
     *
     * @param locationDTO the entity to save.
     * @return the persisted entity.
     */
    public LocationDTO update(LocationDTO locationDTO) {
        log.debug("Request to update Location : {}", locationDTO);
        Location location = locationMapper.toEntity(locationDTO);
        location = locationRepository.save(location);
        return locationMapper.toDto(location);
    }

    public LocationDTO update(LocationDTO locationDTO, User user) {
        Long userId = user.getId();
        Location location = null;

        log.debug("Request to update Location for User ID: {}", userId);

        try {
            Librarian librarian = librarianRepository
                .findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Librarian not found for user id: " + userId));
            location = librarian.getLocation();
            log.debug("Found Location for Librarian with User ID: {}", userId);
        } catch (EntityNotFoundException e) {
            log.warn("Librarian not found for User ID: {}. Trying to find Visitor.", userId);

            try {
                Visitor visitor = visitorRepository
                    .findByUserId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Visitor not found for user id: " + userId));
                location = visitor.getAddress();
                log.debug("Found Location for Visitor with User ID: {}", userId);
            } catch (EntityNotFoundException ex) {
                log.error("No Librarian or Visitor found for User ID: {}", userId);
                throw new EntityNotFoundException("No Librarian or Visitor found for user id: " + userId);
            }
        }

        if (location != null) {
            locationDTO.setId(location.getId());
            location = locationMapper.toEntity(locationDTO);
            location = locationRepository.save(location);
            log.debug("Updated Location with ID: {}", location.getId());
            return locationMapper.toDto(location);
        }

        log.error("Location could not be updated because it was not found.");
        throw new EntityNotFoundException("Location could not be updated because it was not found.");
    }

    /**
     * Partially update a location.
     *
     * @param locationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LocationDTO> partialUpdate(LocationDTO locationDTO) {
        log.debug("Request to partially update Location : {}", locationDTO);

        return locationRepository
            .findById(locationDTO.getId())
            .map(existingLocation -> {
                locationMapper.partialUpdate(existingLocation, locationDTO);

                return existingLocation;
            })
            .map(locationRepository::save)
            .map(locationMapper::toDto);
    }

    /**
     * Get all the locations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<LocationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Locations");
        return locationRepository.findAll(pageable).map(locationMapper::toDto);
    }

    /**
     * Get one location by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LocationDTO> findOne(Long id) {
        log.debug("Request to get Location : {}", id);
        return locationRepository.findById(id).map(locationMapper::toDto);
    }

    /**
     * Delete the location by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Location : {}", id);
        locationRepository.deleteById(id);
    }
}
