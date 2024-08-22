package com.xtramile.library2024.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.xtramile.library2024.domain.File} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FileDTO implements Serializable {

    private Long id;

    private String image;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FileDTO)) {
            return false;
        }

        FileDTO fileDTO = (FileDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, fileDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FileDTO{" +
            "id=" + getId() +
            ", image='" + getImage() + "'" +
            "}";
    }
}
