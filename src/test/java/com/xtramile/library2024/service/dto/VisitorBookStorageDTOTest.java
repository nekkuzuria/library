package com.xtramile.library2024.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.xtramile.library2024.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VisitorBookStorageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(VisitorBookStorageDTO.class);
        VisitorBookStorageDTO visitorBookStorageDTO1 = new VisitorBookStorageDTO();
        visitorBookStorageDTO1.setId(1L);
        VisitorBookStorageDTO visitorBookStorageDTO2 = new VisitorBookStorageDTO();
        assertThat(visitorBookStorageDTO1).isNotEqualTo(visitorBookStorageDTO2);
        visitorBookStorageDTO2.setId(visitorBookStorageDTO1.getId());
        assertThat(visitorBookStorageDTO1).isEqualTo(visitorBookStorageDTO2);
        visitorBookStorageDTO2.setId(2L);
        assertThat(visitorBookStorageDTO1).isNotEqualTo(visitorBookStorageDTO2);
        visitorBookStorageDTO1.setId(null);
        assertThat(visitorBookStorageDTO1).isNotEqualTo(visitorBookStorageDTO2);
    }
}
