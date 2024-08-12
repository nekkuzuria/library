package com.xtramile.library2024.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.xtramile.library2024.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BookStorageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookStorageDTO.class);
        BookStorageDTO bookStorageDTO1 = new BookStorageDTO();
        bookStorageDTO1.setId(1L);
        BookStorageDTO bookStorageDTO2 = new BookStorageDTO();
        assertThat(bookStorageDTO1).isNotEqualTo(bookStorageDTO2);
        bookStorageDTO2.setId(bookStorageDTO1.getId());
        assertThat(bookStorageDTO1).isEqualTo(bookStorageDTO2);
        bookStorageDTO2.setId(2L);
        assertThat(bookStorageDTO1).isNotEqualTo(bookStorageDTO2);
        bookStorageDTO1.setId(null);
        assertThat(bookStorageDTO1).isNotEqualTo(bookStorageDTO2);
    }
}
