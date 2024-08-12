package com.xtramile.library2024.domain;

import static com.xtramile.library2024.domain.LibrarianTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.xtramile.library2024.web.rest.TestUtil;
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
}
