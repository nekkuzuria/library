package com.xtramile.library2024.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Visit.
 */
@Entity
@Table(name = "visit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Visit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "visits" }, allowSetters = true)
    private Library library;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "visits" }, allowSetters = true)
    private Librarian librarian;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "visitorBookStorages", "visits" }, allowSetters = true)
    private Visitor visitor;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "visits", "visitor", "book" }, allowSetters = true)
    private VisitorBookStorage visitorBookStorage;

    public Long getId() {
        return this.id;
    }

    public Visit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public Visit date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Library getLibrary() {
        return this.library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public Visit library(Library library) {
        this.setLibrary(library);
        return this;
    }

    public Librarian getLibrarian() {
        return this.librarian;
    }

    public void setLibrarian(Librarian librarian) {
        this.librarian = librarian;
    }

    public Visit librarian(Librarian librarian) {
        this.setLibrarian(librarian);
        return this;
    }

    public Visitor getVisitor() {
        return this.visitor;
    }

    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    public Visit visitor(Visitor visitor) {
        this.setVisitor(visitor);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    public VisitorBookStorage getVisitorBookStorage() {
        return visitorBookStorage;
    }

    public void setVisitorBookStorage(VisitorBookStorage visitorBookStorage) {
        this.visitorBookStorage = visitorBookStorage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Visit)) {
            return false;
        }
        return getId() != null && getId().equals(((Visit) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Visit{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            "}";
    }
}
