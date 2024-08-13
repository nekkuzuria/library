package com.xtramile.library2024.service.mapper;

import com.xtramile.library2024.domain.Library;
import com.xtramile.library2024.domain.Location;
import com.xtramile.library2024.domain.Visitor;
import com.xtramile.library2024.service.dto.LibraryDTO;
import com.xtramile.library2024.service.dto.LocationDTO;
import com.xtramile.library2024.service.dto.VisitorDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Visitor} and its DTO {@link VisitorDTO}.
 */
@Mapper(componentModel = "spring")
public interface VisitorMapper extends EntityMapper<VisitorDTO, Visitor> {
    @Mapping(target = "address", source = "address", qualifiedByName = "locationId")
    @Mapping(target = "library", source = "library", qualifiedByName = "libraryId")
    VisitorDTO toDto(Visitor s);

    @Named("locationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LocationDTO toDtoLocationId(Location location);

    @Named("libraryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LibraryDTO toDtoLibraryId(Library library);
}
