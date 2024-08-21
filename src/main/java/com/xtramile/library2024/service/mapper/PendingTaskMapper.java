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

    default PendingTaskVM toVm(PendingTask e) {
        return new PendingTaskVM(
            e.getId(),
            e.getStatus(),
            e.getVisitor().getId(),
            e.getVisitor().getName(),
            e.getBook().getId(),
            e.getBook().getTitle(),
            e.getBook().getType(),
            e.getBook().getCover(),
            e.getBook().getAuthor(),
            e.getQuantity()
        );
    }
}
