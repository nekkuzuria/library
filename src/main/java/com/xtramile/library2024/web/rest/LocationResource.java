package com.xtramile.library2024.web.rest;

import com.xtramile.library2024.repository.LocationRepository;
import com.xtramile.library2024.service.LocationService;
import com.xtramile.library2024.service.dto.LocationDTO;
import com.xtramile.library2024.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.xtramile.library2024.domain.Location}.
 */
@RestController
@RequestMapping("/api/locations")
public class LocationResource {

    private static final Logger log = LoggerFactory.getLogger(LocationResource.class);

    private static final String ENTITY_NAME = "location";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LocationService locationService;

    private final LocationRepository locationRepository;

    public LocationResource(LocationService locationService, LocationRepository locationRepository) {
        this.locationService = locationService;
        this.locationRepository = locationRepository;
    }

    /**
     * {@code POST  /locations} : Create a new location.
     *
     * @param locationDTO the locationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new locationDTO, or with status {@code 400 (Bad Request)} if the location has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LocationDTO> createLocation(@Valid @RequestBody LocationDTO locationDTO) throws URISyntaxException {
        log.debug("REST request to save Location : {}", locationDTO);
        if (locationDTO.getId() != null) {
            log.error("Attempted to create a new Location with an existing ID: {}", locationDTO.getId());
            throw new BadRequestAlertException("A new location cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LocationDTO savedLocationDTO = locationService.save(locationDTO);
        log.info("Location created successfully with ID: {}", savedLocationDTO.getId());
        return ResponseEntity.created(new URI("/api/locations/" + savedLocationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, savedLocationDTO.getId().toString()))
            .body(locationDTO);
    }

    /**
     * {@code PUT  /locations/:id} : Updates an existing location.
     *
     * @param id the id of the locationDTO to save.
     * @param locationDTO the locationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated locationDTO,
     * or with status {@code 400 (Bad Request)} if the locationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the locationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LocationDTO> updateLocation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LocationDTO locationDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Location : {}, {}", id, locationDTO);
        if (locationDTO.getId() == null) {
            log.error("Invalid ID: Location ID is null");
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, locationDTO.getId())) {
            log.error("ID mismatch: Path variable ID {} does not match Location ID {}", id, locationDTO.getId());
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!locationRepository.existsById(id)) {
            log.error("Entity not found: No Location exists with ID {}", id);
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LocationDTO updatedLocationDTO = locationService.update(locationDTO);
        log.info("Location updated successfully with ID: {}", updatedLocationDTO.getId());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, updatedLocationDTO.getId().toString()))
            .body(locationDTO);
    }

    /**
     * {@code PATCH  /locations/:id} : Partial updates given fields of an existing location, field will ignore if it is null
     *
     * @param id the id of the locationDTO to save.
     * @param locationDTO the locationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated locationDTO,
     * or with status {@code 400 (Bad Request)} if the locationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the locationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the locationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LocationDTO> partialUpdateLocation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LocationDTO locationDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Location partially : {}, {}", id, locationDTO);
        if (locationDTO.getId() == null) {
            log.error("Invalid ID: Location ID is null");
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, locationDTO.getId())) {
            log.error("ID mismatch: Path variable ID {} does not match Location ID {}", id, locationDTO.getId());
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!locationRepository.existsById(id)) {
            log.error("Entity not found: No Location exists with ID {}", id);
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LocationDTO> result = locationService.partialUpdate(locationDTO);
        if (result.isPresent()) {
            log.info("Location partially updated with ID: {}", result.get().getId());
        } else {
            log.warn("Partial update failed: No Location found with ID {}", id);
        }

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, locationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /locations} : get all the locations.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of locations in body.
     */
    @GetMapping("")
    public ResponseEntity<List<LocationDTO>> getAllLocations(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Locations");
        Page<LocationDTO> page = locationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        log.info("Retrieved {} Locations", page.getTotalElements());
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /locations/:id} : get the "id" location.
     *
     * @param id the id of the locationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the locationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getLocation(@PathVariable("id") Long id) {
        log.debug("REST request to get Location : {}", id);
        Optional<LocationDTO> locationDTO = locationService.findOne(id);
        if (locationDTO.isPresent()) {
            log.info("Location found with ID: {}", id);
        } else {
            log.warn("Location not found with ID: {}", id);
        }
        return ResponseUtil.wrapOrNotFound(locationDTO);
    }

    /**
     * {@code DELETE  /locations/:id} : delete the "id" location.
     *
     * @param id the id of the locationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable("id") Long id) {
        log.debug("REST request to delete Location : {}", id);
        if (locationService.findOne(id).isPresent()) {
            locationService.delete(id);
            log.info("Location deleted with ID: {}", id);
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                .build();
        } else {
            log.error("Attempted to delete non-existent Location with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
}
