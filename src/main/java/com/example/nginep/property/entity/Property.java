package com.example.nginep.property.entity;

import com.example.nginep.room.entity.Room;
import com.example.nginep.users.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "properties")
@NoArgsConstructor
@Getter
@Setter
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Users tenant;

    @Column(name = "property_name", nullable = false, length = 100)
    private String propertyName;

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "property_description")
    private String propertyDescription;

    @Column(name = "place-type", nullable = false, length = 255)
    private String placeType;

    @Column(name = "property_address", nullable = false, length = 100)
    private String propertyAddress;

    @Column(name = "property_city", nullable = false, length = 100)
    private String propertyCity;

    @Column(name = "property_province", nullable = false, length = 20)
    private String propertyProvince;

    @Column(name = "property_postal_code")
    private String propertyPostalCode;

    @Column(name = "property_latitude", nullable = false)
    private String propertyLatitude;

    @Column(name = "property_longitude", nullable = false, length = 20)
    private String propertyLongitude;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "not_available_from")
    private Instant notAvailableFrom;

    @Column(name = "not_available_to")
    private Instant notAvailableTo;

    @Column(name = "peak_season_from")
    private Instant peakSeasonFrom;

    @Column(name = "peak_season_to")
    private Instant peakSeasonTo;

    @Column(name = "property_category")
    private String propertyCategory;

    @Column(name = "increment_type")
    private String incrementType;

    @Column(name = "amount")
    private BigDecimal amount;

    @OneToMany(mappedBy = "property")
    private List<Room> rooms;

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