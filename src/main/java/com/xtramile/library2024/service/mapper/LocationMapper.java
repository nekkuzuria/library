package com.xtramile.library2024.service.mapper;

import com.xtramile.library2024.domain.Location;
import com.xtramile.library2024.service.dto.LocationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Location} and its DTO {@link LocationDTO}.
 */
@Mapper(componentModel = "spring")
public interface LocationMapper extends EntityMapper<LocationDTO, Location> {}
