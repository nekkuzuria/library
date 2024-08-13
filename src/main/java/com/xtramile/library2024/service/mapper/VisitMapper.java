package com.xtramile.library2024.service.mapper;

import com.xtramile.library2024.domain.Librarian;
import com.xtramile.library2024.domain.Library;
import com.xtramile.library2024.domain.Visit;
import com.xtramile.library2024.domain.Visitor;
import com.xtramile.library2024.service.dto.LibrarianDTO;
import com.xtramile.library2024.service.dto.LibraryDTO;
import com.xtramile.library2024.service.dto.VisitDTO;
import com.xtramile.library2024.service.dto.VisitorDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Visit} and its DTO {@link VisitDTO}.
 */
@Mapper(componentModel = "spring")
public interface VisitMapper extends EntityMapper<VisitDTO, Visit> {
    @Mapping(target = "library", source = "library", qualifiedByName = "libraryId")
    @Mapping(target = "librarian", source = "librarian", qualifiedByName = "librarianId")
    @Mapping(target = "visitor", source = "visitor", qualifiedByName = "visitorId")
    VisitDTO toDto(Visit s);

    @Named("libraryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LibraryDTO toDtoLibraryId(Library library);

    @Named("librarianId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LibrarianDTO toDtoLibrarianId(Librarian librarian);

    @Named("visitorId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    VisitorDTO toDtoVisitorId(Visitor visitor);
}
