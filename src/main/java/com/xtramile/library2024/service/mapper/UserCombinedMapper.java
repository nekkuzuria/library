package com.xtramile.library2024.service.mapper;

import com.xtramile.library2024.domain.File;
import com.xtramile.library2024.service.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserCombinedMapper {
    @Mapping(target = "id", source = "librarian.id")
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

    @Mapping(target = "name", source = "userVisitorDTO", qualifiedByName = "combineNameVisitor")
    @Mapping(target = "email", source = "userVisitorDTO.email")
    @Mapping(target = "phoneNumber", source = "userVisitorDTO.phoneNumber")
    @Mapping(target = "dateOfBirth", source = "userVisitorDTO.dateOfBirth")
    VisitorDTO toVisitorDTO(UserVisitorDTO userVisitorDTO);

    @Mapping(target = "name", source = "userLibrarianDTO", qualifiedByName = "combineNameLibrarian")
    @Mapping(target = "email", source = "userLibrarianDTO.email")
    @Mapping(target = "phoneNumber", source = "userLibrarianDTO.phoneNumber")
    @Mapping(target = "dateOfBirth", source = "userLibrarianDTO.dateOfBirth")
    LibrarianDTO toLibrarianDTO(UserLibrarianDTO userLibrarianDTO);

    @Mapping(target = "streetAddress", source = "userVisitorDTO.streetAddress")
    @Mapping(target = "posttalCode", source = "userVisitorDTO.postalCode")
    @Mapping(target = "city", source = "userVisitorDTO.city")
    @Mapping(target = "stateProvince", source = "userVisitorDTO.stateProvince")
    LocationDTO toLocationDTO(UserVisitorDTO userVisitorDTO);

    @Mapping(target = "streetAddress", source = "userLibrarianDTO.streetAddress")
    @Mapping(target = "posttalCode", source = "userLibrarianDTO.postalCode")
    @Mapping(target = "city", source = "userLibrarianDTO.city")
    @Mapping(target = "stateProvince", source = "userLibrarianDTO.stateProvince")
    LocationDTO toLocationDTO(UserLibrarianDTO userLibrarianDTO);

    @Named("combineNameVisitor")
    default String combineNameVisitor(UserVisitorDTO userVisitorDTO) {
        if (userVisitorDTO == null) {
            return null;
        }
        return userVisitorDTO.getFirstName() + " " + userVisitorDTO.getLastName();
    }

    @Named("combineNameLibrarian")
    default String combineNameLibrarian(UserLibrarianDTO userLibrarianDTO) {
        if (userLibrarianDTO == null) {
            return null;
        }
        return userLibrarianDTO.getFirstName() + " " + userLibrarianDTO.getLastName();
    }
}
