package com.xtramile.library2024.service.mapper;

import static com.xtramile.library2024.domain.VisitorAsserts.*;
import static com.xtramile.library2024.domain.VisitorTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VisitorMapperTest {

    private VisitorMapper visitorMapper;

    @BeforeEach
    void setUp() {
        visitorMapper = new VisitorMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getVisitorSample1();
        var actual = visitorMapper.toEntity(visitorMapper.toDto(expected));
        assertVisitorAllPropertiesEquals(expected, actual);
    }
}
