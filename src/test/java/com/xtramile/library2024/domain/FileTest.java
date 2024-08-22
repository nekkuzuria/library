package com.xtramile.library2024.domain;

import static com.xtramile.library2024.domain.FileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.xtramile.library2024.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(File.class);
        File file1 = getFileSample1();
        File file2 = new File();
        assertThat(file1).isNotEqualTo(file2);

        file2.setId(file1.getId());
        assertThat(file1).isEqualTo(file2);

        file2 = getFileSample2();
        assertThat(file1).isNotEqualTo(file2);
    }
}
