package com.xtramile.library2024.domain;

import static com.xtramile.library2024.domain.LibrarianTestSamples.*;
import static com.xtramile.library2024.domain.LibraryTestSamples.*;
import static com.xtramile.library2024.domain.LocationTestSamples.*;
import static com.xtramile.library2024.domain.VisitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.xtramile.library2024.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class LibrarianTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Librarian.class);
        Librarian librarian1 = getLibrarianSample1();
        Librarian librarian2 = new Librarian();
        assertThat(librarian1).isNotEqualTo(librarian2);

        librarian2.setId(librarian1.getId());
        assertThat(librarian1).isEqualTo(librarian2);

        librarian2 = getLibrarianSample2();
        assertThat(librarian1).isNotEqualTo(librarian2);
    }

    @Test
    void libraryTest() {
        Librarian librarian = getLibrarianRandomSampleGenerator();
        Library libraryBack = getLibraryRandomSampleGenerator();

        librarian.setLibrary(libraryBack);
        assertThat(librarian.getLibrary()).isEqualTo(libraryBack);

        librarian.library(null);
        assertThat(librarian.getLibrary()).isNull();
    }

    @Test
    void locationTest() {
        Librarian librarian = getLibrarianRandomSampleGenerator();
        Location locationBack = getLocationRandomSampleGenerator();

        librarian.setLocation(locationBack);
        assertThat(librarian.getLocation()).isEqualTo(locationBack);

        librarian.location(null);
        assertThat(librarian.getLocation()).isNull();
    }

    @Test
    void visitTest() {
        Librarian librarian = getLibrarianRandomSampleGenerator();
        Visit visitBack = getVisitRandomSampleGenerator();

        librarian.addVisit(visitBack);
        assertThat(librarian.getVisits()).containsOnly(visitBack);
        assertThat(visitBack.getLibrarian()).isEqualTo(librarian);

        librarian.removeVisit(visitBack);
        assertThat(librarian.getVisits()).doesNotContain(visitBack);
        assertThat(visitBack.getLibrarian()).isNull();

        librarian.visits(new HashSet<>(Set.of(visitBack)));
        assertThat(librarian.getVisits()).containsOnly(visitBack);
        assertThat(visitBack.getLibrarian()).isEqualTo(librarian);

        librarian.setVisits(new HashSet<>());
        assertThat(librarian.getVisits()).doesNotContain(visitBack);
        assertThat(visitBack.getLibrarian()).isNull();
    }
}
