package com.xtramile.library2024.domain;

import static com.xtramile.library2024.domain.LibrarianTestSamples.*;
import static com.xtramile.library2024.domain.LibraryTestSamples.*;
import static com.xtramile.library2024.domain.VisitTestSamples.*;
import static com.xtramile.library2024.domain.VisitorTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.xtramile.library2024.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VisitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Visit.class);
        Visit visit1 = getVisitSample1();
        Visit visit2 = new Visit();
        assertThat(visit1).isNotEqualTo(visit2);

        visit2.setId(visit1.getId());
        assertThat(visit1).isEqualTo(visit2);

        visit2 = getVisitSample2();
        assertThat(visit1).isNotEqualTo(visit2);
    }

    @Test
    void libraryTest() {
        Visit visit = getVisitRandomSampleGenerator();
        Library libraryBack = getLibraryRandomSampleGenerator();

        visit.setLibrary(libraryBack);
        assertThat(visit.getLibrary()).isEqualTo(libraryBack);

        visit.library(null);
        assertThat(visit.getLibrary()).isNull();
    }

    @Test
    void librarianTest() {
        Visit visit = getVisitRandomSampleGenerator();
        Librarian librarianBack = getLibrarianRandomSampleGenerator();

        visit.setLibrarian(librarianBack);
        assertThat(visit.getLibrarian()).isEqualTo(librarianBack);

        visit.librarian(null);
        assertThat(visit.getLibrarian()).isNull();
    }

    @Test
    void visitorTest() {
        Visit visit = getVisitRandomSampleGenerator();
        Visitor visitorBack = getVisitorRandomSampleGenerator();

        visit.setVisitor(visitorBack);
        assertThat(visit.getVisitor()).isEqualTo(visitorBack);

        visit.visitor(null);
        assertThat(visit.getVisitor()).isNull();
    }
}
