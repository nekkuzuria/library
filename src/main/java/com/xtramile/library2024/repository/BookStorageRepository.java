package com.xtramile.library2024.repository;

import com.xtramile.library2024.domain.BookStorage;
import com.xtramile.library2024.domain.Library;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BookStorage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BookStorageRepository extends JpaRepository<BookStorage, Long> {
    List<BookStorage> findByLibrary(Library library);
}
