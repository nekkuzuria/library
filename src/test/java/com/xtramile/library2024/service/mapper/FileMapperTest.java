package com.xtramile.library2024.service.mapper;

import static com.xtramile.library2024.domain.FileAsserts.*;
import static com.xtramile.library2024.domain.FileTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileMapperTest {

    private FileMapper fileMapper;

    @BeforeEach
    void setUp() {
        fileMapper = new FileMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFileSample1();
        var actual = fileMapper.toEntity(fileMapper.toDto(expected));
        assertFileAllPropertiesEquals(expected, actual);
    }
}
