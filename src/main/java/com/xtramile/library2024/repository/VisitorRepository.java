package com.xtramile.library2024.repository;

import com.xtramile.library2024.domain.Visitor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Visitor entity.
 */
@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    default Optional<Visitor> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Visitor> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Visitor> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select visitor from Visitor visitor left join fetch visitor.user",
        countQuery = "select count(visitor) from Visitor visitor"
    )
    Page<Visitor> findAllWithToOneRelationships(Pageable pageable);

    @Query("select visitor from Visitor visitor left join fetch visitor.user")
    List<Visitor> findAllWithToOneRelationships();

    @Query("select visitor from Visitor visitor left join fetch visitor.user where visitor.id =:id")
    Optional<Visitor> findOneWithToOneRelationships(@Param("id") Long id);

    @Query("select visitor from Visitor visitor where visitor.user.id = :userId")
    Optional<Visitor> findByUserId(@Param("userId") Long userId);
}
