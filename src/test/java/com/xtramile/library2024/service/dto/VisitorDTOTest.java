package com.xtramile.library2024.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.xtramile.library2024.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VisitorDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(VisitorDTO.class);
        VisitorDTO visitorDTO1 = new VisitorDTO();
        visitorDTO1.setId(1L);
        VisitorDTO visitorDTO2 = new VisitorDTO();
        assertThat(visitorDTO1).isNotEqualTo(visitorDTO2);
        visitorDTO2.setId(visitorDTO1.getId());
        assertThat(visitorDTO1).isEqualTo(visitorDTO2);
        visitorDTO2.setId(2L);
        assertThat(visitorDTO1).isNotEqualTo(visitorDTO2);
        visitorDTO1.setId(null);
        assertThat(visitorDTO1).isNotEqualTo(visitorDTO2);
    }
}
