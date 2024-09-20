package com.example.nginep.peakSeasonRates.entity;

import com.example.nginep.property.entity.Property;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "peak_season_rates")
@NoArgsConstructor
@Getter
@Setter
public class PeakSeasonRates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "from", column = @Column(name = "peak_season_from")),
            @AttributeOverride(name = "to", column = @Column(name = "peak_season_to"))
    })
    private DateRange peakSeasonDates;

    @Enumerated(EnumType.STRING)
    @Column(name = "rate_type", nullable = false)
    private RateType rateType;

    @NotNull
    @Column(name = "rate_value", nullable = false)
    private BigDecimal rateValue;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

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

    public enum RateType {
        PERCENTAGE,
        FIXED_AMOUNT
    }
}
