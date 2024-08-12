package com.xtramile.library2024.repository;

import com.xtramile.library2024.domain.Librarian;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Librarian entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LibrarianRepository extends JpaRepository<Librarian, Long> {}
