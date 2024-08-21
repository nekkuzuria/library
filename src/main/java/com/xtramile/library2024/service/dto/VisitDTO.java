package com.xtramile.library2024.service.dto;

import java.io.Serializable;
import java.time.Instant;
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

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
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
            ", library=" + getLibrary() + "'" +
            ", librarian=" + getLibrarian() + "'" +
            ", visitor=" + getVisitor() + "'" +
            ", visitorBookStorage=" + getVisitorBookStorage() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate=" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate=" + getLastModifiedDate() + "'" +
            '}';
    }
}
