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
 * A Visitor.
 */
@Entity
@Table(name = "visitor")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Visitor extends AbstractAuditingEntity implements Serializable {

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

    @Column(name = "membership_status")
    private Boolean membershipStatus;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "visitor")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "visitor", "book" }, allowSetters = true)
    private Set<VisitorBookStorage> visitorBookStorages = new HashSet<>();

    @JsonIgnoreProperties(value = { "visitor", "librarian", "library" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Location address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "location", "visitors", "librarians", "bookStorages", "visits" }, allowSetters = true)
    private Library library;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "visitor")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "library", "librarian", "visitor" }, allowSetters = true)
    private Set<Visit> visits = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Visitor id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Visitor name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public Visitor email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Visitor phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return this.dateOfBirth;
    }

    public Visitor dateOfBirth(LocalDate dateOfBirth) {
        this.setDateOfBirth(dateOfBirth);
        return this;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Boolean getMembershipStatus() {
        return this.membershipStatus;
    }

    public Visitor membershipStatus(Boolean membershipStatus) {
        this.setMembershipStatus(membershipStatus);
        return this;
    }

    public void setMembershipStatus(Boolean membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public Set<VisitorBookStorage> getVisitorBookStorages() {
        return this.visitorBookStorages;
    }

    public void setVisitorBookStorages(Set<VisitorBookStorage> visitorBookStorages) {
        if (this.visitorBookStorages != null) {
            this.visitorBookStorages.forEach(i -> i.setVisitor(null));
        }
        if (visitorBookStorages != null) {
            visitorBookStorages.forEach(i -> i.setVisitor(this));
        }
        this.visitorBookStorages = visitorBookStorages;
    }

    public Visitor visitorBookStorages(Set<VisitorBookStorage> visitorBookStorages) {
        this.setVisitorBookStorages(visitorBookStorages);
        return this;
    }

    public Visitor addVisitorBookStorage(VisitorBookStorage visitorBookStorage) {
        this.visitorBookStorages.add(visitorBookStorage);
        visitorBookStorage.setVisitor(this);
        return this;
    }

    public Visitor removeVisitorBookStorage(VisitorBookStorage visitorBookStorage) {
        this.visitorBookStorages.remove(visitorBookStorage);
        visitorBookStorage.setVisitor(null);
        return this;
    }

    public Location getAddress() {
        return this.address;
    }

    public void setAddress(Location location) {
        this.address = location;
    }

    public Visitor address(Location location) {
        this.setAddress(location);
        return this;
    }

    public Library getLibrary() {
        return this.library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public Visitor library(Library library) {
        this.setLibrary(library);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Visitor user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Visit> getVisits() {
        return this.visits;
    }

    public void setVisits(Set<Visit> visits) {
        if (this.visits != null) {
            this.visits.forEach(i -> i.setVisitor(null));
        }
        if (visits != null) {
            visits.forEach(i -> i.setVisitor(this));
        }
        this.visits = visits;
    }

    public Visitor visits(Set<Visit> visits) {
        this.setVisits(visits);
        return this;
    }

    public Visitor addVisit(Visit visit) {
        this.visits.add(visit);
        visit.setVisitor(this);
        return this;
    }

    public Visitor removeVisit(Visit visit) {
        this.visits.remove(visit);
        visit.setVisitor(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Visitor)) {
            return false;
        }
        return getId() != null && getId().equals(((Visitor) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Visitor{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", dateOfBirth='" + getDateOfBirth() + "'" +
            ", membershipStatus='" + getMembershipStatus() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate=" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate=" + getLastModifiedDate() + "'" +
            '}';
    }
}
