package com.example.nginep.property.entity;

import com.example.nginep.propertyFacility.entity.PropertyFacility;
import com.example.nginep.propertyImages.entity.PropertyImage;
import com.example.nginep.rooms.entity.Room;
import com.example.nginep.users.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

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
    @Column(nullable = false)
    private Long id;

    @NotNull
    @Column(name = "property_name", nullable = false)
    private String propertyName;

    @NotNull
    @Column(name = "property_category", nullable = false)
    private String propertyCategory;

    @NotNull
    @Column(name = "property_description", nullable = false)
    private String propertyDescription;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyFacility> propertyFacilities;

    @NotNull
    @Column(name="place-type", nullable = false)
    private String guestPlaceType;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyImage> propertyImages;

    @NotNull
    @Column(name = "property_address", nullable = false)
    private String propertyAddress;

    @NotNull
    @Column(name = "property_city", nullable = false)
    private String propertyCity;

    @NotNull
    @Column(name = "property_province", nullable = false)
    private String propertyProvince;

    @NotNull
    @Column(name = "property_postal_code", nullable = false)
    private String propertyPostalCode;

    @NotNull
    @Column(name = "property_latitude", nullable = false)
    private Double propertyLatitude;

    @NotNull
    @Column(name = "property_longitude", nullable = false)
    private Double propertyLongitude;

    @OneToMany(mappedBy = "property",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms;

    @OneToMany(mappedBy = "property",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PeakSeasonRate> peakSeasonRates;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Users user;

//    @Embedded
//    @AttributeOverrides({
//            @AttributeOverride(name = "from", column = @Column(name = "not_available_from")),
//            @AttributeOverride(name = "to", column = @Column(name = "not_available_to"))
//    })
//    private DateRange notAvailabilityDates;

//    @Embedded
//    @AttributeOverrides({
//            @AttributeOverride(name = "from", column = @Column(name = "peak_season_from")),
//            @AttributeOverride(name = "to", column = @Column(name = "peak_season_to"))
//    })
//    private DateRange peakSeasonDates;



//    @Embedded
//    @AttributeOverrides({
//            @AttributeOverride(name = "incrementType", column = @Column(name = "increment_type")),
//            @AttributeOverride(name = "amount", column = @Column(name = "amount"))
//    })
//    private PeakSeasonRate peakSeasonRate;



    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    @PreRemove
    public void preRemove() {
        this.deletedAt = Instant.now();
    }
}

