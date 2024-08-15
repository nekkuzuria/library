package com.xtramile.library2024.web.rest;

import com.xtramile.library2024.repository.VisitorRepository;
import com.xtramile.library2024.service.VisitorService;
import com.xtramile.library2024.service.dto.VisitorDTO;
import com.xtramile.library2024.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.xtramile.library2024.domain.Visitor}.
 */
@RestController
@RequestMapping("/api/visitors")
public class VisitorResource {

    private static final Logger log = LoggerFactory.getLogger(VisitorResource.class);

    private static final String ENTITY_NAME = "visitor";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VisitorService visitorService;

    private final VisitorRepository visitorRepository;

    public VisitorResource(VisitorService visitorService, VisitorRepository visitorRepository) {
        this.visitorService = visitorService;
        this.visitorRepository = visitorRepository;
    }

    /**
     * {@code POST  /visitors} : Create a new visitor.
     *
     * @param visitorDTO the visitorDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new visitorDTO, or with status {@code 400 (Bad Request)} if the visitor has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<VisitorDTO> createVisitor(@RequestBody VisitorDTO visitorDTO) throws URISyntaxException {
        log.debug("REST request to save Visitor : {}", visitorDTO);
        if (visitorDTO.getId() != null) {
            throw new BadRequestAlertException("A new visitor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        visitorDTO = visitorService.save(visitorDTO);
        return ResponseEntity.created(new URI("/api/visitors/" + visitorDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, visitorDTO.getId().toString()))
            .body(visitorDTO);
    }

    /**
     * {@code PUT  /visitors/:id} : Updates an existing visitor.
     *
     * @param id the id of the visitorDTO to save.
     * @param visitorDTO the visitorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated visitorDTO,
     * or with status {@code 400 (Bad Request)} if the visitorDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the visitorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VisitorDTO> updateVisitor(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody VisitorDTO visitorDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Visitor : {}, {}", id, visitorDTO);
        if (visitorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, visitorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!visitorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        visitorDTO = visitorService.update(visitorDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, visitorDTO.getId().toString()))
            .body(visitorDTO);
    }

    /**
     * {@code PATCH  /visitors/:id} : Partial updates given fields of an existing visitor, field will ignore if it is null
     *
     * @param id the id of the visitorDTO to save.
     * @param visitorDTO the visitorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated visitorDTO,
     * or with status {@code 400 (Bad Request)} if the visitorDTO is not valid,
     * or with status {@code 404 (Not Found)} if the visitorDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the visitorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VisitorDTO> partialUpdateVisitor(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody VisitorDTO visitorDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Visitor partially : {}, {}", id, visitorDTO);
        if (visitorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, visitorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!visitorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VisitorDTO> result = visitorService.partialUpdate(visitorDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, visitorDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /visitors} : get all the visitors.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of visitors in body.
     */
    @GetMapping("")
    public List<VisitorDTO> getAllVisitors(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        log.debug("REST request to get all Visitors");
        return visitorService.findAll();
    }

    /**
     * {@code GET  /visitors/:id} : get the "id" visitor.
     *
     * @param id the id of the visitorDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the visitorDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VisitorDTO> getVisitor(@PathVariable("id") Long id) {
        log.debug("REST request to get Visitor : {}", id);
        Optional<VisitorDTO> visitorDTO = visitorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(visitorDTO);
    }

    /**
     * {@code DELETE  /visitors/:id} : delete the "id" visitor.
     *
     * @param id the id of the visitorDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisitor(@PathVariable("id") Long id) {
        log.debug("REST request to delete Visitor : {}", id);
        visitorService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
