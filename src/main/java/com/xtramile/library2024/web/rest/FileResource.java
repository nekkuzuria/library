package com.xtramile.library2024.web.rest;

import com.xtramile.library2024.domain.File;
import com.xtramile.library2024.repository.FileRepository;
import com.xtramile.library2024.service.BookService;
import com.xtramile.library2024.service.FileService;
import com.xtramile.library2024.service.UserService;
import com.xtramile.library2024.service.dto.FileDTO;
import com.xtramile.library2024.web.rest.errors.BadRequestAlertException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.xtramile.library2024.domain.File}.
 */
@RestController
@RequestMapping("/api/files")
public class FileResource {

    private static final Logger log = LoggerFactory.getLogger(FileResource.class);

    private static final String ENTITY_NAME = "file";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FileService fileService;

    private final FileRepository fileRepository;
    private final UserService userService;
    private final BookService bookService;

    public FileResource(FileService fileService, FileRepository fileRepository, UserService userService, BookService bookService) {
        this.fileService = fileService;
        this.fileRepository = fileRepository;
        this.userService = userService;
        this.bookService = bookService;
    }

    /**
     * {@code POST  /files} : Create a new file.
     *
     * @param fileDTO the fileDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fileDTO, or with status {@code 400 (Bad Request)} if the file has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FileDTO> createFile(@RequestBody FileDTO fileDTO) throws URISyntaxException {
        log.debug("REST request to save File : {}", fileDTO);
        if (fileDTO.getId() != null) {
            log.error("Attempted to create a new file with an existing ID: {}", fileDTO.getId());
            throw new BadRequestAlertException("A new file cannot already have an ID", ENTITY_NAME, "idexists");
        }
        fileDTO = fileService.save(fileDTO);
        log.info("File created successfully with ID: {}", fileDTO.getId());
        return ResponseEntity.created(new URI("/api/files/" + fileDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, fileDTO.getId().toString()))
            .body(fileDTO);
    }

    /**
     * {@code PUT  /files/:id} : Updates an existing file.
     *
     * @param id the id of the fileDTO to save.
     * @param fileDTO the fileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fileDTO,
     * or with status {@code 400 (Bad Request)} if the fileDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FileDTO> updateFile(@PathVariable(value = "id", required = false) final Long id, @RequestBody FileDTO fileDTO)
        throws URISyntaxException {
        log.debug("REST request to update File : {}, {}", id, fileDTO);
        if (fileDTO.getId() == null) {
            log.error("Invalid ID: FileDTO ID is null");
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fileDTO.getId())) {
            log.error("ID mismatch: Path variable ID {} does not match FileDTO ID {}", id, fileDTO.getId());
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fileRepository.existsById(id)) {
            log.error("Entity not found: No File exists with ID {}", id);
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        fileDTO = fileService.update(fileDTO);
        log.info("File updated successfully with ID: {}", fileDTO.getId());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, fileDTO.getId().toString()))
            .body(fileDTO);
    }

    /**
     * {@code PATCH  /files/:id} : Partial updates given fields of an existing file, field will ignore if it is null
     *
     * @param id the id of the fileDTO to save.
     * @param fileDTO the fileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fileDTO,
     * or with status {@code 400 (Bad Request)} if the fileDTO is not valid,
     * or with status {@code 404 (Not Found)} if the fileDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the fileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FileDTO> partialUpdateFile(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FileDTO fileDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update File partially : {}, {}", id, fileDTO);
        if (fileDTO.getId() == null) {
            log.error("Invalid ID: FileDTO ID is null");
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fileDTO.getId())) {
            log.error("ID mismatch: Path variable ID {} does not match FileDTO ID {}", id, fileDTO.getId());
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fileRepository.existsById(id)) {
            log.error("Entity not found: No File exists with ID {}", id);
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FileDTO> result = fileService.partialUpdate(fileDTO);
        if (result.isPresent()) {
            log.info("File partially updated with ID: {}", fileDTO.getId());
        } else {
            log.warn("No File updated with ID: {}", fileDTO.getId());
        }

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, fileDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /files} : get all the files.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of files in body.
     */
    @GetMapping("")
    public List<FileDTO> getAllFiles() {
        log.debug("REST request to get all Files");
        List<FileDTO> files = fileService.findAll();
        log.info("Retrieved {} Files", files.size());
        return files;
    }

    /**
     * {@code GET  /files/:id} : get the "id" file.
     *
     * @param id the id of the fileDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fileDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FileDTO> getFile(@PathVariable("id") Long id) {
        log.debug("REST request to get File : {}", id);
        Optional<FileDTO> fileDTO = fileService.findOne(id);
        if (fileDTO.isPresent()) {
            log.info("File found with ID: {}", id);
        } else {
            log.warn("File not found with ID: {}", id);
        }
        return ResponseUtil.wrapOrNotFound(fileDTO);
    }

    /**
     * {@code DELETE  /files/:id} : delete the "id" file.
     *
     * @param id the id of the fileDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable("id") Long id) {
        log.debug("REST request to delete File : {}", id);
        if (fileService.findOne(id).isPresent()) {
            fileService.delete(id);
            log.info("File deleted with ID: {}", id);
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                .build();
        } else {
            log.error("Attempted to delete non-existent File with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/upload-user-image")
    public ResponseEntity<File> uploadUserImage(@RequestParam("file") MultipartFile file) throws IOException {
        log.debug("REST request to upload user image");
        File savedFile = fileService.saveImage(file);
        userService.updateUserImage(savedFile);
        log.info("User image uploaded successfully with id: {}", savedFile.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFile);
    }

    @PostMapping("/upload-book-image")
    public ResponseEntity<File> uploadBookImage(@RequestParam("id") Long id, @RequestParam("file") MultipartFile file) throws IOException {
        log.debug("REST request to upload book image for ID: {}", id);
        File savedFile = fileService.saveImage(file);
        bookService.updateBookImage(id, savedFile);
        log.info("Book image uploaded successfully for book ID: {}", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFile);
    }

    @GetMapping("/image")
    public ResponseEntity<byte[]> getUserImage() {
        log.debug("REST request to get user image");
        byte[] imageData = fileService.getImage();
        log.info("User image retrieved successfully");
        return ResponseEntity.ok().header("Content-Type", "image/jpeg").body(imageData);
    }
}
