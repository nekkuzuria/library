package com.xtramile.library2024.service.mapper;

import com.xtramile.library2024.domain.Librarian;
import com.xtramile.library2024.service.dto.LibrarianDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Librarian} and its DTO {@link LibrarianDTO}.
 */
@Mapper(componentModel = "spring")
public interface LibrarianMapper extends EntityMapper<LibrarianDTO, Librarian> {}
