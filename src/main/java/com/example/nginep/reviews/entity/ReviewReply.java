package com.example.nginep.reviews.entity;

import com.example.nginep.users.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "review_replies")
public class ReviewReply {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_replies_id_gen")
    @SequenceGenerator(name = "review_replies_id_gen", sequenceName = "review_replies_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Users tenant;

    @Column(name = "reply", nullable = false)
    private String reply;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}