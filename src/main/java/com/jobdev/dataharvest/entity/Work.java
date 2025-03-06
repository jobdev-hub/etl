package com.jobdev.dataharvest.entity;

import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "work")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "refKey")
public class Work {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private UUID id;

    @Column(name = "ref_key", unique = true, updatable = false, nullable = false)
    private String refKey;

    @Column(name = "title", nullable = false, length = 1000)
    private String title;

    @ManyToMany(cascade = { CascadeType.MERGE })
    @JoinTable(name = "work_author", joinColumns = @JoinColumn(name = "work_id"), inverseJoinColumns = @JoinColumn(name = "author_id"))
    private Set<Author> authors;

}
