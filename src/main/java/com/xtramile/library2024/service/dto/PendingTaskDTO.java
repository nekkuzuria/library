package com.xtramile.library2024.service.dto;

import com.xtramile.library2024.domain.enumeration.PendingTaskStatus;
import com.xtramile.library2024.domain.enumeration.PendingTaskType;
import java.time.Instant;
import java.util.Objects;

public class PendingTaskDTO {

    private Long id;

    private PendingTaskType type;

    private PendingTaskStatus status;

    private LibraryDTO library;

    private VisitorDTO visitor;

    private BookDTO book;

    private Integer quantity;

    private LibrarianDTO librarian;

    private VisitorBookStorageDTO visitorBookStorage;

    private String reason;

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

    public PendingTaskStatus getStatus() {
        return status;
    }

    public void setStatus(PendingTaskStatus status) {
        this.status = status;
    }

    public LibraryDTO getLibrary() {
        return library;
    }

    public void setLibrary(LibraryDTO library) {
        this.library = library;
    }

    public VisitorDTO getVisitor() {
        return visitor;
    }

    public void setVisitor(VisitorDTO visitor) {
        this.visitor = visitor;
    }

    public BookDTO getBook() {
        return book;
    }

    public void setBook(BookDTO book) {
        this.book = book;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public PendingTaskType getType() {
        return type;
    }

    public void setType(PendingTaskType type) {
        this.type = type;
    }

    public LibrarianDTO getLibrarian() {
        return librarian;
    }

    public void setLibrarian(LibrarianDTO librarian) {
        this.librarian = librarian;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public VisitorBookStorageDTO getVisitorBookStorage() {
        return visitorBookStorage;
    }

    public void setVisitorBookStorage(VisitorBookStorageDTO visitorBookStorage) {
        this.visitorBookStorage = visitorBookStorage;
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
        if (!(o instanceof PendingTaskDTO)) {
            return false;
        }

        PendingTaskDTO pendingTaskDTO = (PendingTaskDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, pendingTaskDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return (
            "PendingTaskDTO{" +
            "id=" +
            getId() +
            ", status=" +
            getStatus() +
            ", library=" +
            getLibrary() +
            ", visitor=" +
            getVisitor() +
            ", book=" +
            getBook() +
            ", quantity=" +
            getQuantity() +
            '}'
        );
    }
}
