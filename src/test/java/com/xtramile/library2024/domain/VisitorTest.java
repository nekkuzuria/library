package com.xtramile.library2024.domain;

import static com.xtramile.library2024.domain.LibraryTestSamples.*;
import static com.xtramile.library2024.domain.LocationTestSamples.*;
import static com.xtramile.library2024.domain.VisitTestSamples.*;
import static com.xtramile.library2024.domain.VisitorBookStorageTestSamples.*;
import static com.xtramile.library2024.domain.VisitorTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.xtramile.library2024.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class VisitorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Visitor.class);
        Visitor visitor1 = getVisitorSample1();
        Visitor visitor2 = new Visitor();
        assertThat(visitor1).isNotEqualTo(visitor2);

        visitor2.setId(visitor1.getId());
        assertThat(visitor1).isEqualTo(visitor2);

        visitor2 = getVisitorSample2();
        assertThat(visitor1).isNotEqualTo(visitor2);
    }

    @Test
    void visitorBookStorageTest() {
        Visitor visitor = getVisitorRandomSampleGenerator();
        VisitorBookStorage visitorBookStorageBack = getVisitorBookStorageRandomSampleGenerator();

        visitor.addVisitorBookStorage(visitorBookStorageBack);
        assertThat(visitor.getVisitorBookStorages()).containsOnly(visitorBookStorageBack);
        assertThat(visitorBookStorageBack.getVisitor()).isEqualTo(visitor);

        visitor.removeVisitorBookStorage(visitorBookStorageBack);
        assertThat(visitor.getVisitorBookStorages()).doesNotContain(visitorBookStorageBack);
        assertThat(visitorBookStorageBack.getVisitor()).isNull();

        visitor.visitorBookStorages(new HashSet<>(Set.of(visitorBookStorageBack)));
        assertThat(visitor.getVisitorBookStorages()).containsOnly(visitorBookStorageBack);
        assertThat(visitorBookStorageBack.getVisitor()).isEqualTo(visitor);

        visitor.setVisitorBookStorages(new HashSet<>());
        assertThat(visitor.getVisitorBookStorages()).doesNotContain(visitorBookStorageBack);
        assertThat(visitorBookStorageBack.getVisitor()).isNull();
    }

    @Test
    void addressTest() {
        Visitor visitor = getVisitorRandomSampleGenerator();
        Location locationBack = getLocationRandomSampleGenerator();

        visitor.setAddress(locationBack);
        assertThat(visitor.getAddress()).isEqualTo(locationBack);

        visitor.address(null);
        assertThat(visitor.getAddress()).isNull();
    }

    @Test
    void libraryTest() {
        Visitor visitor = getVisitorRandomSampleGenerator();
        Library libraryBack = getLibraryRandomSampleGenerator();

        visitor.setLibrary(libraryBack);
        assertThat(visitor.getLibrary()).isEqualTo(libraryBack);

        visitor.library(null);
        assertThat(visitor.getLibrary()).isNull();
    }

    @Test
    void visitTest() {
        Visitor visitor = getVisitorRandomSampleGenerator();
        Visit visitBack = getVisitRandomSampleGenerator();

        visitor.addVisit(visitBack);
        assertThat(visitor.getVisits()).containsOnly(visitBack);
        assertThat(visitBack.getVisitor()).isEqualTo(visitor);

        visitor.removeVisit(visitBack);
        assertThat(visitor.getVisits()).doesNotContain(visitBack);
        assertThat(visitBack.getVisitor()).isNull();

        visitor.visits(new HashSet<>(Set.of(visitBack)));
        assertThat(visitor.getVisits()).containsOnly(visitBack);
        assertThat(visitBack.getVisitor()).isEqualTo(visitor);

        visitor.setVisits(new HashSet<>());
        assertThat(visitor.getVisits()).doesNotContain(visitBack);
        assertThat(visitBack.getVisitor()).isNull();
    }
}
