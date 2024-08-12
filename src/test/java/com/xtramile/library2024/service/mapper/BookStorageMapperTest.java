package com.xtramile.library2024.service.mapper;

import static com.xtramile.library2024.domain.BookStorageAsserts.*;
import static com.xtramile.library2024.domain.BookStorageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookStorageMapperTest {

    private BookStorageMapper bookStorageMapper;

    @BeforeEach
    void setUp() {
        bookStorageMapper = new BookStorageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBookStorageSample1();
        var actual = bookStorageMapper.toEntity(bookStorageMapper.toDto(expected));
        assertBookStorageAllPropertiesEquals(expected, actual);
    }
}
