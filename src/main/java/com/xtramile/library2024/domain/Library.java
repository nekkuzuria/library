package com.xtramile.library2024.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Library.
 */
@Entity
@Table(name = "library")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Library extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "established_date")
    private LocalDate establishedDate;

    @JsonIgnoreProperties(value = { "library" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Location location;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "library")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "books", "library" }, allowSetters = true)
    private Set<BookStorage> bookStorages = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "library")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "library", "librarian", "visitor" }, allowSetters = true)
    private Set<Visit> visits = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Library id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Library name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getEstablishedDate() {
        return this.establishedDate;
    }

    public Library establishedDate(LocalDate establishedDate) {
        this.setEstablishedDate(establishedDate);
        return this;
    }

    public void setEstablishedDate(LocalDate establishedDate) {
        this.establishedDate = establishedDate;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Library location(Location location) {
        this.setLocation(location);
        return this;
    }

    public Set<BookStorage> getBookStorages() {
        return this.bookStorages;
    }

    public void setBookStorages(Set<BookStorage> bookStorages) {
        if (this.bookStorages != null) {
            this.bookStorages.forEach(i -> i.setLibrary(null));
        }
        if (bookStorages != null) {
            bookStorages.forEach(i -> i.setLibrary(this));
        }
        this.bookStorages = bookStorages;
    }

    public Library bookStorages(Set<BookStorage> bookStorages) {
        this.setBookStorages(bookStorages);
        return this;
    }

    public Library addBookStorage(BookStorage bookStorage) {
        this.bookStorages.add(bookStorage);
        bookStorage.setLibrary(this);
        return this;
    }

    public Library removeBookStorage(BookStorage bookStorage) {
        this.bookStorages.remove(bookStorage);
        bookStorage.setLibrary(null);
        return this;
    }

    public Set<Visit> getVisits() {
        return this.visits;
    }

    public void setVisits(Set<Visit> visits) {
        if (this.visits != null) {
            this.visits.forEach(i -> i.setLibrary(null));
        }
        if (visits != null) {
            visits.forEach(i -> i.setLibrary(this));
        }
        this.visits = visits;
    }

    public Library visits(Set<Visit> visits) {
        this.setVisits(visits);
        return this;
    }

    public Library addVisit(Visit visit) {
        this.visits.add(visit);
        visit.setLibrary(this);
        return this;
    }

    public Library removeVisit(Visit visit) {
        this.visits.remove(visit);
        visit.setLibrary(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Library)) {
            return false;
        }
        return getId() != null && getId().equals(((Library) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Library{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", establishedDate='" + getEstablishedDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate=" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate=" + getLastModifiedDate() + "'" +
            '}';
    }
}
