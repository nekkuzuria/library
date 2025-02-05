package com.xtramile.library2024.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xtramile.library2024.domain.enumeration.BookType;
import com.xtramile.library2024.domain.enumeration.Genre;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Book.
 */
@Entity
@Table(name = "book")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Book extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private BookType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "genre")
    private Genre genre;

    @Column(name = "year")
    private Integer year;

    @Column(name = "total_page")
    private Integer totalPage;

    @Column(name = "author")
    private String author;

    @Column(name = "cover")
    private String cover;

    @Column(name = "synopsis")
    private String synopsis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "books", "library" }, allowSetters = true)
    private BookStorage bookStorage;

    @JsonIgnoreProperties(value = { "book" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private File file;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Book id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Book title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BookType getType() {
        return this.type;
    }

    public Book type(BookType type) {
        this.setType(type);
        return this;
    }

    public void setType(BookType type) {
        this.type = type;
    }

    public Genre getGenre() {
        return this.genre;
    }

    public Book genre(Genre genre) {
        this.setGenre(genre);
        return this;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Integer getYear() {
        return this.year;
    }

    public Book year(Integer year) {
        this.setYear(year);
        return this;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getTotalPage() {
        return this.totalPage;
    }

    public Book totalPage(Integer totalPage) {
        this.setTotalPage(totalPage);
        return this;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public String getAuthor() {
        return this.author;
    }

    public Book author(String author) {
        this.setAuthor(author);
        return this;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCover() {
        return this.cover;
    }

    public Book cover(String cover) {
        this.setCover(cover);
        return this;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getSynopsis() {
        return this.synopsis;
    }

    public Book synopsis(String synopsis) {
        this.setSynopsis(synopsis);
        return this;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public BookStorage getBookStorage() {
        return this.bookStorage;
    }

    public void setBookStorage(BookStorage bookStorage) {
        this.bookStorage = bookStorage;
    }

    public Book bookStorage(BookStorage bookStorage) {
        this.setBookStorage(bookStorage);
        return this;
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Book file(File file) {
        this.setFile(file);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        return getId() != null && getId().equals(((Book) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Book{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", type='" + getType() + "'" +
            ", genre='" + getGenre() + "'" +
            ", year=" + getYear() +
            ", totalPage=" + getTotalPage() +
            ", author='" + getAuthor() + "'" +
            ", cover='" + getCover() + "'" +
            ", synopsis='" + getSynopsis() + "'" +
            "}";
    }
}
