package com.xtramile.library2024.repository;

import com.xtramile.library2024.domain.VisitorBookStorage;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the VisitorBookStorage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VisitorBookStorageRepository extends JpaRepository<VisitorBookStorage, Long> {}
