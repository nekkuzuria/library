package com.xtramile.library2024.repository;

import com.xtramile.library2024.domain.Book;
import com.xtramile.library2024.domain.BookStorage;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Book entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b WHERE b.bookStorage IN :bookStorages")
    Page<Book> findByBookStorages(@Param("bookStorages") List<BookStorage> bookStorages, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.bookStorage.id IN :bookStorageIds")
    Page<Book> findByBookStorageIds(@Param("bookStorageIds") List<Long> bookStorageIds, Pageable pageable);
}
