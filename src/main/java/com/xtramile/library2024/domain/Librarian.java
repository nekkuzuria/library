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
 * A Librarian.
 */
@Entity
@Table(name = "librarian")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Librarian implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "librarians", "bookStorages", "visits" }, allowSetters = true)
    private Library library;

    @JsonIgnoreProperties(value = { "librarian" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Location location;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "librarian")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "library", "librarian", "visitor" }, allowSetters = true)
    private Set<Visit> visits = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Librarian id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Librarian name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public Librarian email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Librarian phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return this.dateOfBirth;
    }

    public Librarian dateOfBirth(LocalDate dateOfBirth) {
        this.setDateOfBirth(dateOfBirth);
        return this;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Library getLibrary() {
        return this.library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public Librarian library(Library library) {
        this.setLibrary(library);
        return this;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Librarian location(Location location) {
        this.setLocation(location);
        return this;
    }

    public Set<Visit> getVisits() {
        return this.visits;
    }

    public void setVisits(Set<Visit> visits) {
        if (this.visits != null) {
            this.visits.forEach(i -> i.setLibrarian(null));
        }
        if (visits != null) {
            visits.forEach(i -> i.setLibrarian(this));
        }
        this.visits = visits;
    }

    public Librarian visits(Set<Visit> visits) {
        this.setVisits(visits);
        return this;
    }

    public Librarian addVisit(Visit visit) {
        this.visits.add(visit);
        visit.setLibrarian(this);
        return this;
    }

    public Librarian removeVisit(Visit visit) {
        this.visits.remove(visit);
        visit.setLibrarian(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Librarian)) {
            return false;
        }
        return getId() != null && getId().equals(((Librarian) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Librarian{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", dateOfBirth='" + getDateOfBirth() + "'" +
            "}";
    }
}
