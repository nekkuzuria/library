package com.xtramile.library2024.service.mapper;

import com.xtramile.library2024.domain.BookStorage;
import com.xtramile.library2024.service.dto.BookStorageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BookStorage} and its DTO {@link BookStorageDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookStorageMapper extends EntityMapper<BookStorageDTO, BookStorage> {}
