package com.xtramile.library2024.service.impl;

import com.xtramile.library2024.domain.Visitor;
import com.xtramile.library2024.repository.VisitorRepository;
import com.xtramile.library2024.security.SecurityUtils;
import com.xtramile.library2024.service.VisitorService;
import com.xtramile.library2024.service.dto.VisitorDTO;
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

    public VisitorServiceImpl(VisitorRepository visitorRepository, VisitorMapper visitorMapper) {
        this.visitorRepository = visitorRepository;
        this.visitorMapper = visitorMapper;
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

    public Page<VisitorDTO> findAllWithEagerRelationships(Pageable pageable) {
        return visitorRepository.findAllWithEagerRelationships(pageable).map(visitorMapper::toDto);
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
        return visitorRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException("Visitor not found for user")).getId();
    }

    @Override
    public VisitorDTO getVisitorOfCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        return visitorRepository.findByUserId(userId).map(visitorMapper::toDto).orElse(null);
    }
}
