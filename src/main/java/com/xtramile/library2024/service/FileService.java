package com.xtramile.library2024.service;

import com.xtramile.library2024.domain.Book;
import com.xtramile.library2024.domain.File;
import com.xtramile.library2024.domain.User;
import com.xtramile.library2024.repository.BookRepository;
import com.xtramile.library2024.repository.FileRepository;
import com.xtramile.library2024.repository.UserRepository;
import com.xtramile.library2024.security.SecurityUtils;
import com.xtramile.library2024.service.dto.FileDTO;
import com.xtramile.library2024.service.mapper.FileMapper;
import java.io.IOException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service Implementation for managing {@link File}.
 */
@Service
@Transactional
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    private FileRepository fileRepository = null;

    private final FileMapper fileMapper;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public FileService(FileRepository fileRepository, FileMapper fileMapper, UserRepository userRepository, BookRepository bookRepository) {
        this.fileRepository = fileRepository;
        this.fileMapper = fileMapper;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Save a file.
     *
     * @param fileDTO the entity to save.
     * @return the persisted entity.
     */
    public FileDTO save(FileDTO fileDTO) {
        log.debug("Request to save File : {}", fileDTO);
        File file = fileMapper.toEntity(fileDTO);
        file = fileRepository.save(file);
        return fileMapper.toDto(file);
    }

    /**
     * Update a file.
     *
     * @param fileDTO the entity to save.
     * @return the persisted entity.
     */
    public FileDTO update(FileDTO fileDTO) {
        log.debug("Request to update File : {}", fileDTO);
        File file = fileMapper.toEntity(fileDTO);
        file = fileRepository.save(file);
        return fileMapper.toDto(file);
    }

    /**
     * Partially update a file.
     *
     * @param fileDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FileDTO> partialUpdate(FileDTO fileDTO) {
        log.debug("Request to partially update File : {}", fileDTO);

        return fileRepository
            .findById(fileDTO.getId())
            .map(existingFile -> {
                fileMapper.partialUpdate(existingFile, fileDTO);

                return existingFile;
            })
            .map(fileRepository::save)
            .map(fileMapper::toDto);
    }

    /**
     * Get all the files.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<FileDTO> findAll() {
        log.debug("Request to get all Files");
        return fileRepository.findAll().stream().map(fileMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one file by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FileDTO> findOne(Long id) {
        log.debug("Request to get File : {}", id);
        return fileRepository.findById(id).map(fileMapper::toDto);
    }

    /**
     * Delete the file by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete File : {}", id);
        fileRepository.deleteById(id);
    }

    public File saveImage(MultipartFile file) throws IOException {
        log.debug("Request to save image with file name: {}", file.getOriginalFilename());
        File fileEntity = new File();
        fileEntity.setImage(Base64.getEncoder().encodeToString(file.getBytes()));
        File savedFileEntity = fileRepository.save(fileEntity);
        return savedFileEntity;
    }

    public byte[] getImage() {
        log.debug("Request to get image");

        String login = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> {
                log.error("Failed to get image: User is not logged in");
                return new RuntimeException("User is not logged in");
            });

        User user = userRepository
            .findOneByLogin(login)
            .orElseThrow(() -> {
                log.error("Failed to get image: User not found for login {}", login);
                return new RuntimeException("User not found");
            });

        File fileEntity = user.getFile();

        if (fileEntity == null) {
            log.error("Failed to get image: No file associated with user {}", login);
            throw new RuntimeException("File not found");
        }

        return Base64.getDecoder().decode(fileEntity.getImage());
    }
}
