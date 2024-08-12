package com.xtramile.library2024.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.xtramile.library2024.domain.Library} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LibraryDTO implements Serializable {

    private Long id;

    private String name;

    private LocalDate establishedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getEstablishedDate() {
        return establishedDate;
    }

    public void setEstablishedDate(LocalDate establishedDate) {
        this.establishedDate = establishedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LibraryDTO)) {
            return false;
        }

        LibraryDTO libraryDTO = (LibraryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, libraryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LibraryDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", establishedDate='" + getEstablishedDate() + "'" +
            "}";
    }
}
