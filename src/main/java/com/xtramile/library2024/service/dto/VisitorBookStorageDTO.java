package com.xtramile.library2024.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.xtramile.library2024.domain.VisitorBookStorage} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VisitorBookStorageDTO implements Serializable {

    private Long id;

    private LocalDate borrowDate;

    private LocalDate returnDate;

    private VisitorDTO visitor;

    private BookDTO book;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VisitorBookStorageDTO)) {
            return false;
        }

        VisitorBookStorageDTO visitorBookStorageDTO = (VisitorBookStorageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, visitorBookStorageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VisitorBookStorageDTO{" +
            "id=" + getId() +
            ", borrowDate='" + getBorrowDate() + "'" +
            ", returnDate='" + getReturnDate() + "'" +
            ", visitor=" + getVisitor() +
            ", book=" + getBook() +
            "}";
    }
}
