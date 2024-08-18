package com.xtramile.library2024.service.mapper;

import com.xtramile.library2024.domain.Book;
import com.xtramile.library2024.domain.Visitor;
import com.xtramile.library2024.domain.VisitorBookStorage;
import com.xtramile.library2024.service.dto.BookDTO;
import com.xtramile.library2024.service.dto.VisitorBookStorageDTO;
import com.xtramile.library2024.service.dto.VisitorDTO;
import com.xtramile.library2024.web.rest.vm.VisitorBookStorageVM;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link VisitorBookStorage} and its DTO {@link VisitorBookStorageDTO}.
 */
@Mapper(componentModel = "spring")
public interface VisitorBookStorageMapper extends EntityMapper<VisitorBookStorageDTO, VisitorBookStorage> {
    @Mapping(target = "visitor", source = "visitor", qualifiedByName = "visitorId")
    @Mapping(target = "book", source = "book", qualifiedByName = "bookId")
    VisitorBookStorageDTO toDto(VisitorBookStorage s);

    @Named("visitorId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    VisitorDTO toDtoVisitorId(Visitor visitor);

    @Named("bookId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BookDTO toDtoBookId(Book book);

    default VisitorBookStorageVM toVm(VisitorBookStorage e) {
        VisitorBookStorageVM vm = new VisitorBookStorageVM(
            e.getId(),
            e.getBook().getId(),
            e.getBook().getTitle(),
            e.getBook().getType(),
            e.getBook().getGenre(),
            e.getBook().getYear(),
            e.getBook().getTotalPage(),
            e.getBook().getAuthor(),
            e.getBook().getCover(),
            e.getBorrowDate(),
            e.getReturnDate()
        );
        return vm;
    }
}
