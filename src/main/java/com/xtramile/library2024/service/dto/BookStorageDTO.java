package com.xtramile.library2024.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.xtramile.library2024.domain.BookStorage} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookStorageDTO implements Serializable {

    private Long id;

    private Integer quantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(o instanceof BookStorageDTO)) {
            return false;
        }

        BookStorageDTO bookStorageDTO = (BookStorageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, bookStorageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookStorageDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            "}";
    }
}
