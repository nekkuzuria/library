package com.xtramile.library2024.domain;

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
}
