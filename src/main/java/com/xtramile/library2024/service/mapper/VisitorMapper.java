package com.xtramile.library2024.service.mapper;

import com.xtramile.library2024.domain.Visitor;
import com.xtramile.library2024.service.dto.VisitorDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Visitor} and its DTO {@link VisitorDTO}.
 */
@Mapper(componentModel = "spring")
public interface VisitorMapper extends EntityMapper<VisitorDTO, Visitor> {}
