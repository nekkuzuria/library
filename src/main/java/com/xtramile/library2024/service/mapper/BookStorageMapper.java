package com.xtramile.library2024.service.mapper;

import com.xtramile.library2024.domain.BookStorage;
import com.xtramile.library2024.domain.Library;
import com.xtramile.library2024.service.dto.BookStorageDTO;
import com.xtramile.library2024.service.dto.LibraryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BookStorage} and its DTO {@link BookStorageDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookStorageMapper extends EntityMapper<BookStorageDTO, BookStorage> {
    @Mapping(target = "library", source = "library", qualifiedByName = "libraryId")
    BookStorageDTO toDto(BookStorage s);

    @Named("libraryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LibraryDTO toDtoLibraryId(Library library);
}
