package com.example.nginep.property.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class PeakSeasonRate {
    private String incrementType;
    private Integer amount;
}
