package com.xtramile.library2024.service.mapper;

import com.xtramile.library2024.domain.Book;
import com.xtramile.library2024.domain.PendingTask;
import com.xtramile.library2024.domain.Visitor;
import com.xtramile.library2024.service.dto.BookDTO;
import com.xtramile.library2024.service.dto.PendingTaskDTO;
import com.xtramile.library2024.service.dto.VisitorDTO;
import com.xtramile.library2024.web.rest.vm.PendingTaskVM;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { VisitorMapper.class, BookMapper.class, LibraryMapper.class })
public interface PendingTaskMapper extends EntityMapper<PendingTaskDTO, PendingTask> {
    @Mapping(target = "visitor", source = "visitor", qualifiedByName = "visitorId")
    @Mapping(target = "book", source = "book", qualifiedByName = "bookId")
    @Mapping(target = "library", source = "library", qualifiedByName = "libraryId")
    PendingTaskDTO toDto(PendingTask s);

    @Named("visitorId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    VisitorDTO toDtoVisitorId(Visitor visitor);

    @Named("bookId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BookDTO toDtoBookId(Book book);

    @Mapping(source = "visitor.id", target = "visitorId")
    @Mapping(source = "visitor.name", target = "visitorName")
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    @Mapping(source = "book.author", target = "bookAuthor")
    @Mapping(source = "librarian.id", target = "librarianId")
    @Mapping(source = "librarian.name", target = "librarianName")
    PendingTaskVM toVm(PendingTask pendingTask);

    @Mapping(source = "visitorId", target = "visitor.id")
    @Mapping(source = "visitorName", target = "visitor.name")
    @Mapping(source = "bookId", target = "book.id")
    @Mapping(source = "bookTitle", target = "book.title")
    @Mapping(source = "bookAuthor", target = "book.author")
    @Mapping(source = "librarianId", target = "librarian.id")
    @Mapping(source = "librarianName", target = "librarian.name")
    PendingTaskDTO toDto(PendingTaskVM vm);
}
