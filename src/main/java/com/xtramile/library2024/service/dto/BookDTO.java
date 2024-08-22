package com.xtramile.library2024.service.dto;

import com.xtramile.library2024.domain.enumeration.BookType;
import com.xtramile.library2024.domain.enumeration.Genre;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.xtramile.library2024.domain.Book} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    private BookType type;

    private Genre genre;

    private Integer year;

    private Integer totalPage;

    private String author;

    private String cover;

    private String synopsis;

    private BookStorageDTO bookStorage;

    private FileDTO file;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BookType getType() {
        return type;
    }

    public void setType(BookType type) {
        this.type = type;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public BookStorageDTO getBookStorage() {
        return bookStorage;
    }

    public void setBookStorage(BookStorageDTO bookStorage) {
        this.bookStorage = bookStorage;
    }

    public FileDTO getFile() {
        return file;
    }

    public void setFile(FileDTO file) {
        this.file = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookDTO)) {
            return false;
        }

        BookDTO bookDTO = (BookDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, bookDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", type='" + getType() + "'" +
            ", genre='" + getGenre() + "'" +
            ", year=" + getYear() +
            ", totalPage=" + getTotalPage() +
            ", author='" + getAuthor() + "'" +
            ", cover='" + getCover() + "'" +
            ", synopsis='" + getSynopsis() + "'" +
            ", bookStorage=" + getBookStorage() +
            ", file=" + getFile() +
            "}";
    }
}
