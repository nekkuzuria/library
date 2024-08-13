package com.xtramile.library2024.domain;

import static com.xtramile.library2024.domain.BookTestSamples.*;
import static com.xtramile.library2024.domain.VisitorBookStorageTestSamples.*;
import static com.xtramile.library2024.domain.VisitorTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.xtramile.library2024.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VisitorBookStorageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VisitorBookStorage.class);
        VisitorBookStorage visitorBookStorage1 = getVisitorBookStorageSample1();
        VisitorBookStorage visitorBookStorage2 = new VisitorBookStorage();
        assertThat(visitorBookStorage1).isNotEqualTo(visitorBookStorage2);

        visitorBookStorage2.setId(visitorBookStorage1.getId());
        assertThat(visitorBookStorage1).isEqualTo(visitorBookStorage2);

        visitorBookStorage2 = getVisitorBookStorageSample2();
        assertThat(visitorBookStorage1).isNotEqualTo(visitorBookStorage2);
    }

    @Test
    void visitorTest() {
        VisitorBookStorage visitorBookStorage = getVisitorBookStorageRandomSampleGenerator();
        Visitor visitorBack = getVisitorRandomSampleGenerator();

        visitorBookStorage.setVisitor(visitorBack);
        assertThat(visitorBookStorage.getVisitor()).isEqualTo(visitorBack);

        visitorBookStorage.visitor(null);
        assertThat(visitorBookStorage.getVisitor()).isNull();
    }

    @Test
    void bookTest() {
        VisitorBookStorage visitorBookStorage = getVisitorBookStorageRandomSampleGenerator();
        Book bookBack = getBookRandomSampleGenerator();

        visitorBookStorage.setBook(bookBack);
        assertThat(visitorBookStorage.getBook()).isEqualTo(bookBack);

        visitorBookStorage.book(null);
        assertThat(visitorBookStorage.getBook()).isNull();
    }
}
