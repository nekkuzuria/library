package com.xtramile.library2024.domain;

import static com.xtramile.library2024.domain.BookStorageTestSamples.*;
import static com.xtramile.library2024.domain.BookTestSamples.*;
import static com.xtramile.library2024.domain.VisitorBookStorageTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.xtramile.library2024.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BookTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Book.class);
        Book book1 = getBookSample1();
        Book book2 = new Book();
        assertThat(book1).isNotEqualTo(book2);

        book2.setId(book1.getId());
        assertThat(book1).isEqualTo(book2);

        book2 = getBookSample2();
        assertThat(book1).isNotEqualTo(book2);
    }

    @Test
    void bookStorageTest() {
        Book book = getBookRandomSampleGenerator();
        BookStorage bookStorageBack = getBookStorageRandomSampleGenerator();

        book.setBookStorage(bookStorageBack);
        assertThat(book.getBookStorage()).isEqualTo(bookStorageBack);

        book.bookStorage(null);
        assertThat(book.getBookStorage()).isNull();
    }
    //    @Test
    //    void visitorBookStorageTest() {
    //        Book book = getBookRandomSampleGenerator();
    //        VisitorBookStorage visitorBookStorageBack = getVisitorBookStorageRandomSampleGenerator();
    //
    //        book.setVisitorBookStorage(visitorBookStorageBack);
    //        assertThat(book.getVisitorBookStorage()).isEqualTo(visitorBookStorageBack);
    //        assertThat(visitorBookStorageBack.getBook()).isEqualTo(book);
    //
    //        book.visitorBookStorage(null);
    //        assertThat(book.getVisitorBookStorage()).isNull();
    //        assertThat(visitorBookStorageBack.getBook()).isNull();
    //    }
}
