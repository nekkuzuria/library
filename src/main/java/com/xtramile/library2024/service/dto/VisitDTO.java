package com.xtramile.library2024.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.xtramile.library2024.domain.Visit} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VisitDTO implements Serializable {

    private Long id;

    private LocalDate date;

    private LibraryDTO library;

    private LibrarianDTO librarian;

    private VisitorDTO visitor;

    private VisitorBookStorageDTO visitorBookStorageDTO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LibraryDTO getLibrary() {
        return library;
    }

    public void setLibrary(LibraryDTO library) {
        this.library = library;
    }

    public LibrarianDTO getLibrarian() {
        return librarian;
    }

    public void setLibrarian(LibrarianDTO librarian) {
        this.librarian = librarian;
    }

    public VisitorDTO getVisitor() {
        return visitor;
    }

    public void setVisitor(VisitorDTO visitor) {
        this.visitor = visitor;
    }

    public VisitorBookStorageDTO getVisitorBookStorage() {
        return visitorBookStorageDTO;
    }

    public void setVisitorBookStorage(VisitorBookStorageDTO visitorBookStorageDTO) {
        this.visitorBookStorageDTO = visitorBookStorageDTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VisitDTO)) {
            return false;
        }

        VisitDTO visitDTO = (VisitDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, visitDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VisitDTO{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", library=" + getLibrary() +
            ", librarian=" + getLibrarian() +
            ", visitor=" + getVisitor() +
            ", visitorBookStorage=" + getVisitorBookStorage() +
            "}";
    }
}
