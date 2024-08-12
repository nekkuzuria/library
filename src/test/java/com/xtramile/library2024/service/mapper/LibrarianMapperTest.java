package com.xtramile.library2024.service.mapper;

import static com.xtramile.library2024.domain.LibrarianAsserts.*;
import static com.xtramile.library2024.domain.LibrarianTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LibrarianMapperTest {

    private LibrarianMapper librarianMapper;

    @BeforeEach
    void setUp() {
        librarianMapper = new LibrarianMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLibrarianSample1();
        var actual = librarianMapper.toEntity(librarianMapper.toDto(expected));
        assertLibrarianAllPropertiesEquals(expected, actual);
    }
}
