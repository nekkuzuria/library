package com.xtramile.library2024.service.mapper;

import static com.xtramile.library2024.domain.VisitAsserts.*;
import static com.xtramile.library2024.domain.VisitTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VisitMapperTest {

    private VisitMapper visitMapper;

    @BeforeEach
    void setUp() {
        visitMapper = new VisitMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getVisitSample1();
        var actual = visitMapper.toEntity(visitMapper.toDto(expected));
        assertVisitAllPropertiesEquals(expected, actual);
    }
}
