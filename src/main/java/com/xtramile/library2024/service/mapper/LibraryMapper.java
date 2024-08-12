package com.xtramile.library2024.service.mapper;

import com.xtramile.library2024.domain.Library;
import com.xtramile.library2024.service.dto.LibraryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Library} and its DTO {@link LibraryDTO}.
 */
@Mapper(componentModel = "spring")
public interface LibraryMapper extends EntityMapper<LibraryDTO, Library> {}
