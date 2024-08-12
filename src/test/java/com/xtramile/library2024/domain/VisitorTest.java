package com.xtramile.library2024.domain;

import static com.xtramile.library2024.domain.VisitorTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.xtramile.library2024.web.rest.TestUtil;
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
}
