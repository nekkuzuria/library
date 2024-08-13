package com.xtramile.library2024.service.mapper;

import com.xtramile.library2024.domain.Librarian;
import com.xtramile.library2024.domain.Library;
import com.xtramile.library2024.domain.Location;
import com.xtramile.library2024.service.dto.LibrarianDTO;
import com.xtramile.library2024.service.dto.LibraryDTO;
import com.xtramile.library2024.service.dto.LocationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Librarian} and its DTO {@link LibrarianDTO}.
 */
@Mapper(componentModel = "spring")
public interface LibrarianMapper extends EntityMapper<LibrarianDTO, Librarian> {
    @Mapping(target = "library", source = "library", qualifiedByName = "libraryId")
    @Mapping(target = "location", source = "location", qualifiedByName = "locationId")
    LibrarianDTO toDto(Librarian s);

    @Named("libraryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LibraryDTO toDtoLibraryId(Library library);

    @Named("locationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LocationDTO toDtoLocationId(Location location);
}
