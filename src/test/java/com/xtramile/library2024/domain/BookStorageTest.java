package com.xtramile.library2024.domain;

import static com.xtramile.library2024.domain.BookStorageTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.xtramile.library2024.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BookStorageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookStorage.class);
        BookStorage bookStorage1 = getBookStorageSample1();
        BookStorage bookStorage2 = new BookStorage();
        assertThat(bookStorage1).isNotEqualTo(bookStorage2);

        bookStorage2.setId(bookStorage1.getId());
        assertThat(bookStorage1).isEqualTo(bookStorage2);

        bookStorage2 = getBookStorageSample2();
        assertThat(bookStorage1).isNotEqualTo(bookStorage2);
    }
}
