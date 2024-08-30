package com.xtramile.library2024.service.mapper;

import static com.xtramile.library2024.domain.VisitorBookStorageAsserts.*;
import static com.xtramile.library2024.domain.VisitorBookStorageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VisitorBookStorageMapperTest {

    private VisitorBookStorageMapper visitorBookStorageMapper;

    @BeforeEach
    void setUp() {
        visitorBookStorageMapper = new VisitorBookStorageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getVisitorBookStorageSample1();
        var actual = visitorBookStorageMapper.toEntity(visitorBookStorageMapper.toDto(expected));
        assertVisitorBookStorageAllPropertiesEquals(expected, actual);
    }
}
