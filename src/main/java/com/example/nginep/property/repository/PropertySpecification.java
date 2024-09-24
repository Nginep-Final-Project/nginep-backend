package com.example.nginep.property.repository;

import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.property.entity.Property;
import com.example.nginep.rooms.entity.Room;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PropertySpecification {
    public static Specification<Property> byPropertyName(String name) {
        return (root, query, criteriaBuilder) ->
                name != null ? criteriaBuilder.like(criteriaBuilder.lower(root.get("propertyName")), "%" + name + "%") : null;
    }

    public static Specification<Property>  byPropertyCategory(String category) {
        return (root, query, criteriaBuilder) ->
                category != null ? criteriaBuilder.equal(criteriaBuilder.lower(root.get("propertyCategory")), category) : null;
    }

    public static Specification<Property>  byPropertyCity(String city) {
        return (root, query, criteriaBuilder) ->
                city != null ? criteriaBuilder.equal(criteriaBuilder.lower(root.get("propertyCity")), city) : null;
    }

    public static Specification<Property>  byAvailableWithinDates(LocalDate checkInDate, LocalDate checkOutDate) {
        return (root, query, criteriaBuilder) -> {
            if (checkInDate == null || checkOutDate == null) {
                return null;
            }

            Join<Property, Room> rooms = root.join("rooms");
            Join<Room, Booking> bookings = rooms.join("bookings");

            return criteriaBuilder.or(
                    criteriaBuilder.lessThan(bookings.get("checkOutDate"), checkInDate),
                    criteriaBuilder.greaterThan(bookings.get("checkInDate"), checkOutDate)
            );
        };
    }

    public static Specification<Property> byTotalGuests(Integer totalGuests) {
        return (root, query, criteriaBuilder) -> {
            if (totalGuests == null) {
                return null;
            }

            Join<Property, Room> rooms = root.join("rooms");
            return criteriaBuilder.greaterThanOrEqualTo(rooms.get("maxGuests"), totalGuests);
        };
    }
}
