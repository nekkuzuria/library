package com.xtramile.library2024.service.mapper;

import com.xtramile.library2024.domain.Library;
import com.xtramile.library2024.domain.Location;
import com.xtramile.library2024.service.dto.LibraryDTO;
import com.xtramile.library2024.service.dto.LocationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Library} and its DTO {@link LibraryDTO}.
 */
@Mapper(componentModel = "spring")
public interface LibraryMapper extends EntityMapper<LibraryDTO, Library> {
    @Mapping(target = "location", source = "location", qualifiedByName = "locationId")
    LibraryDTO toDto(Library s);

    @Named("locationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LocationDTO toDtoLocationId(Location location);
}
