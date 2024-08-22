package com.xtramile.library2024.service.mapper;

import com.xtramile.library2024.domain.Book;
import com.xtramile.library2024.domain.BookStorage;
import com.xtramile.library2024.domain.File;
import com.xtramile.library2024.service.dto.BookDTO;
import com.xtramile.library2024.service.dto.BookStorageDTO;
import com.xtramile.library2024.service.dto.FileDTO;
import com.xtramile.library2024.web.rest.vm.BookVM;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Book} and its DTO {@link BookDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookMapper extends EntityMapper<BookDTO, Book> {
    @Mapping(target = "bookStorage", source = "bookStorage", qualifiedByName = "bookStorageId")
    @Mapping(target = "file", source = "file", qualifiedByName = "fileId")
    BookDTO toDto(Book s);

    @Named("bookStorageId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BookStorageDTO toDtoBookStorageId(BookStorage bookStorage);

    @Named("fileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FileDTO toDtoFileId(File file);

    @Mapping(source = "bookStorage.id", target = "bookStorageId")
    @Mapping(source = "bookStorage.quantity", target = "quantity")
    BookVM toVM(Book book);

    @Mapping(source = "bookStorageId", target = "bookStorage.id")
    @Mapping(source = "quantity", target = "bookStorage.quantity")
    Book toEntity(BookVM bookVM);

    @Mapping(source = "bookStorageId", target = "bookStorage.id")
    @Mapping(source = "quantity", target = "bookStorage.quantity")
    BookDTO toDTO(BookVM bookVM);
}
