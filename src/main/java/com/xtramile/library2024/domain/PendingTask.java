package com.xtramile.library2024.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xtramile.library2024.domain.enumeration.PendingTaskStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "pending_task")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PendingTask {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PendingTaskStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "visitorBookStorages", "address", "user", "visits", "bookStorages", "visitors" }, allowSetters = true)
    private Library library;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "visitorBookStorages", "address", "library", "user", "visits" }, allowSetters = true)
    private Visitor visitor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "bookStorage" }, allowSetters = true)
    private Book book;

    @Column(name = "quantity")
    private Integer quantity;

    public Long getId() {
        return id;
    }

    public PendingTask id(Long id) {
        this.setId(id);
        return this;
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

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public PendingTask visitor(Visitor visitor) {
        this.setVisitor(visitor);
        return this;
    }

    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    public Book getBook() {
        return book;
    }

    public PendingTask book(Book book) {
        this.setBook(book);
        return this;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return (
            "PendingTask{" +
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
