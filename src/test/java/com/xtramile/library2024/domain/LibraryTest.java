package com.xtramile.library2024.domain;

import static com.xtramile.library2024.domain.BookStorageTestSamples.*;
import static com.xtramile.library2024.domain.LibraryTestSamples.*;
import static com.xtramile.library2024.domain.LocationTestSamples.*;
import static com.xtramile.library2024.domain.VisitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.xtramile.library2024.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class LibraryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Library.class);
        Library library1 = getLibrarySample1();
        Library library2 = new Library();
        assertThat(library1).isNotEqualTo(library2);

        library2.setId(library1.getId());
        assertThat(library1).isEqualTo(library2);

        library2 = getLibrarySample2();
        assertThat(library1).isNotEqualTo(library2);
    }

    @Test
    void locationTest() {
        Library library = getLibraryRandomSampleGenerator();
        Location locationBack = getLocationRandomSampleGenerator();

        library.setLocation(locationBack);
        assertThat(library.getLocation()).isEqualTo(locationBack);

        library.location(null);
        assertThat(library.getLocation()).isNull();
    }

    @Test
    void bookStorageTest() {
        Library library = getLibraryRandomSampleGenerator();
        BookStorage bookStorageBack = getBookStorageRandomSampleGenerator();

        library.addBookStorage(bookStorageBack);
        assertThat(library.getBookStorages()).containsOnly(bookStorageBack);
        assertThat(bookStorageBack.getLibrary()).isEqualTo(library);

        library.removeBookStorage(bookStorageBack);
        assertThat(library.getBookStorages()).doesNotContain(bookStorageBack);
        assertThat(bookStorageBack.getLibrary()).isNull();

        library.bookStorages(new HashSet<>(Set.of(bookStorageBack)));
        assertThat(library.getBookStorages()).containsOnly(bookStorageBack);
        assertThat(bookStorageBack.getLibrary()).isEqualTo(library);

        library.setBookStorages(new HashSet<>());
        assertThat(library.getBookStorages()).doesNotContain(bookStorageBack);
        assertThat(bookStorageBack.getLibrary()).isNull();
    }

    @Test
    void visitTest() {
        Library library = getLibraryRandomSampleGenerator();
        Visit visitBack = getVisitRandomSampleGenerator();

        library.addVisit(visitBack);
        assertThat(library.getVisits()).containsOnly(visitBack);
        assertThat(visitBack.getLibrary()).isEqualTo(library);

        library.removeVisit(visitBack);
        assertThat(library.getVisits()).doesNotContain(visitBack);
        assertThat(visitBack.getLibrary()).isNull();

        library.visits(new HashSet<>(Set.of(visitBack)));
        assertThat(library.getVisits()).containsOnly(visitBack);
        assertThat(visitBack.getLibrary()).isEqualTo(library);

        library.setVisits(new HashSet<>());
        assertThat(library.getVisits()).doesNotContain(visitBack);
        assertThat(visitBack.getLibrary()).isNull();
    }
}
