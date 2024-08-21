package com.xtramile.library2024.service.dto;

import com.xtramile.library2024.domain.enumeration.PendingTaskStatus;
import java.util.Objects;

public class PendingTaskDTO {

    private Long id;

    private PendingTaskStatus status;

    private LibraryDTO library;

    private VisitorDTO visitor;

    private BookDTO book;

    private Integer quantity;

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
