package com.xtramile.library2024.service.mapper;

import static com.xtramile.library2024.domain.LibraryAsserts.*;
import static com.xtramile.library2024.domain.LibraryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LibraryMapperTest {

    private LibraryMapper libraryMapper;

    @BeforeEach
    void setUp() {
        libraryMapper = new LibraryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLibrarySample1();
        var actual = libraryMapper.toEntity(libraryMapper.toDto(expected));
        assertLibraryAllPropertiesEquals(expected, actual);
    }
}
