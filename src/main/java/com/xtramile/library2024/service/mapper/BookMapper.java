package com.xtramile.library2024.service.mapper;

import com.xtramile.library2024.domain.Book;
import com.xtramile.library2024.service.dto.BookDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Book} and its DTO {@link BookDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookMapper extends EntityMapper<BookDTO, Book> {}
