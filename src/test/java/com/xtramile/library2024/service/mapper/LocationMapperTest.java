package com.xtramile.library2024.service.mapper;

import static com.xtramile.library2024.domain.LocationAsserts.*;
import static com.xtramile.library2024.domain.LocationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LocationMapperTest {

    private LocationMapper locationMapper;

    @BeforeEach
    void setUp() {
        locationMapper = new LocationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLocationSample1();
        var actual = locationMapper.toEntity(locationMapper.toDto(expected));
        assertLocationAllPropertiesEquals(expected, actual);
    }
}
