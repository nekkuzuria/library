package com.xtramile.library2024.service.mapper;

import com.xtramile.library2024.domain.Visit;
import com.xtramile.library2024.service.dto.VisitDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Visit} and its DTO {@link VisitDTO}.
 */
@Mapper(componentModel = "spring")
public interface VisitMapper extends EntityMapper<VisitDTO, Visit> {}
