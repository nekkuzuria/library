package com.xtramile.library2024.service.mapper;

import com.xtramile.library2024.domain.*;
import com.xtramile.library2024.service.dto.*;
import com.xtramile.library2024.web.rest.vm.VisitVM;
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

    default VisitVM toVm(Visit visit) {
        VisitVM vm = new VisitVM(
            visit.getId(),
            visit.getVisitorBookStorage().getBook().getId(),
            visit.getVisitorBookStorage().getBook().getTitle(),
            visit.getVisitorBookStorage().getBook().getType(),
            visit.getVisitorBookStorage().getBook().getAuthor(),
            visit.getVisitorBookStorage().getBook().getCover(),
            visit.getVisitorBookStorage().getId(),
            visit.getVisitorBookStorage().getBorrowDate(),
            visit.getVisitorBookStorage().getReturnDate(),
            visit.getLibrarian().getId(),
            visit.getLibrarian().getName(),
            visit.getVisitor().getId(),
            visit.getVisitor().getName(),
            visit.getVisitorBookStorage().getQuantity()
        );
        return vm;
    }
}
