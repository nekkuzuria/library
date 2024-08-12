package com.xtramile.library2024.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.xtramile.library2024.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LibrarianDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LibrarianDTO.class);
        LibrarianDTO librarianDTO1 = new LibrarianDTO();
        librarianDTO1.setId(1L);
        LibrarianDTO librarianDTO2 = new LibrarianDTO();
        assertThat(librarianDTO1).isNotEqualTo(librarianDTO2);
        librarianDTO2.setId(librarianDTO1.getId());
        assertThat(librarianDTO1).isEqualTo(librarianDTO2);
        librarianDTO2.setId(2L);
        assertThat(librarianDTO1).isNotEqualTo(librarianDTO2);
        librarianDTO1.setId(null);
        assertThat(librarianDTO1).isNotEqualTo(librarianDTO2);
    }
}
