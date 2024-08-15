package com.xtramile.library2024.repository;

import com.xtramile.library2024.domain.Librarian;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Librarian entity.
 */
@Repository
public interface LibrarianRepository extends JpaRepository<Librarian, Long> {
    default Optional<Librarian> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Librarian> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Librarian> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select librarian from Librarian librarian left join fetch librarian.user",
        countQuery = "select count(librarian) from Librarian librarian"
    )
    Page<Librarian> findAllWithToOneRelationships(Pageable pageable);

    @Query("select librarian from Librarian librarian left join fetch librarian.user")
    List<Librarian> findAllWithToOneRelationships();

    @Query("select librarian from Librarian librarian left join fetch librarian.user where librarian.id =:id")
    Optional<Librarian> findOneWithToOneRelationships(@Param("id") Long id);
}
