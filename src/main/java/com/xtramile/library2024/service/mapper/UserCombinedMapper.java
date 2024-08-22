package com.xtramile.library2024.service.mapper;

import com.xtramile.library2024.domain.File;
import com.xtramile.library2024.service.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserCombinedMapper {
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "username", source = "user.login")
    @Mapping(target = "phoneNumber", source = "librarian.phoneNumber")
    @Mapping(target = "dateOfBirth", source = "librarian.dateOfBirth")
    @Mapping(target = "streetAddress", source = "location.streetAddress")
    @Mapping(target = "postalCode", source = "location.posttalCode")
    @Mapping(target = "city", source = "location.city")
    @Mapping(target = "stateProvince", source = "location.stateProvince")
    @Mapping(target = "image", source = "file.image")
    UserLibrarianDTO toCombinedResponseLibrarianDTO(
        @MappingTarget UserLibrarianDTO dto,
        AdminUserDTO user,
        LibrarianDTO librarian,
        LocationDTO location,
        FileDTO file
    );

    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "username", source = "user.login")
    @Mapping(target = "phoneNumber", source = "visitor.phoneNumber")
    @Mapping(target = "dateOfBirth", source = "visitor.dateOfBirth")
    @Mapping(target = "streetAddress", source = "location.streetAddress")
    @Mapping(target = "postalCode", source = "location.posttalCode")
    @Mapping(target = "city", source = "location.city")
    @Mapping(target = "stateProvince", source = "location.stateProvince")
    @Mapping(target = "image", source = "file.image")
    UserVisitorDTO toCombinedResponseVisitorDTO(
        @MappingTarget UserVisitorDTO dto,
        AdminUserDTO user,
        VisitorDTO visitor,
        LocationDTO location,
        FileDTO file
    );
}
