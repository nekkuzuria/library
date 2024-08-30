package com.xtramile.library2024.domain;

import static com.xtramile.library2024.domain.BookStorageTestSamples.*;
import static com.xtramile.library2024.domain.BookTestSamples.*;
import static com.xtramile.library2024.domain.LibraryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.xtramile.library2024.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BookStorageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookStorage.class);
        BookStorage bookStorage1 = getBookStorageSample1();
        BookStorage bookStorage2 = new BookStorage();
        assertThat(bookStorage1).isNotEqualTo(bookStorage2);

        bookStorage2.setId(bookStorage1.getId());
        assertThat(bookStorage1).isEqualTo(bookStorage2);

        bookStorage2 = getBookStorageSample2();
        assertThat(bookStorage1).isNotEqualTo(bookStorage2);
    }

    @Test
    void bookTest() {
        BookStorage bookStorage = getBookStorageRandomSampleGenerator();
        Book bookBack = getBookRandomSampleGenerator();

        bookStorage.addBook(bookBack);
        assertThat(bookStorage.getBooks()).containsOnly(bookBack);
        assertThat(bookBack.getBookStorage()).isEqualTo(bookStorage);

        bookStorage.removeBook(bookBack);
        assertThat(bookStorage.getBooks()).doesNotContain(bookBack);
        assertThat(bookBack.getBookStorage()).isNull();

        bookStorage.books(new HashSet<>(Set.of(bookBack)));
        assertThat(bookStorage.getBooks()).containsOnly(bookBack);
        assertThat(bookBack.getBookStorage()).isEqualTo(bookStorage);

        bookStorage.setBooks(new HashSet<>());
        assertThat(bookStorage.getBooks()).doesNotContain(bookBack);
        assertThat(bookBack.getBookStorage()).isNull();
    }

    @Test
    void libraryTest() {
        BookStorage bookStorage = getBookStorageRandomSampleGenerator();
        Library libraryBack = getLibraryRandomSampleGenerator();

        bookStorage.setLibrary(libraryBack);
        assertThat(bookStorage.getLibrary()).isEqualTo(libraryBack);

        bookStorage.library(null);
        assertThat(bookStorage.getLibrary()).isNull();
    }
}
