package com.xtramile.library2024.repository;

import com.xtramile.library2024.domain.Library;
import com.xtramile.library2024.domain.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Visit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    Page<Visit> findByLibrary(Library library, Pageable pageable);
}
