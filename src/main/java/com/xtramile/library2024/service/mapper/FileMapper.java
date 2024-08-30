package com.xtramile.library2024.service.mapper;

import com.xtramile.library2024.domain.File;
import com.xtramile.library2024.service.dto.FileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link File} and its DTO {@link FileDTO}.
 */
@Mapper(componentModel = "spring")
public interface FileMapper extends EntityMapper<FileDTO, File> {}
