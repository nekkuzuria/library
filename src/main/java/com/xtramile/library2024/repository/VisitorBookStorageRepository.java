package com.xtramile.library2024.repository;

import com.xtramile.library2024.domain.Visitor;
import com.xtramile.library2024.domain.VisitorBookStorage;
import com.xtramile.library2024.service.dto.VisitorDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the VisitorBookStorage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VisitorBookStorageRepository extends JpaRepository<VisitorBookStorage, Long> {
    Page<VisitorBookStorage> findByVisitor(Visitor visitor, Pageable pageable);
}
