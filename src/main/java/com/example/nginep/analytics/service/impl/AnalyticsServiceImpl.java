package com.example.nginep.analytics.service.impl;

import com.example.nginep.analytics.dto.EarningsByPropertyDto;
import com.example.nginep.analytics.dto.EarningsByTransactionDto;
import com.example.nginep.analytics.dto.OverviewReportDto;
import com.example.nginep.analytics.dto.PropertyAvailabilityDto;
import com.example.nginep.analytics.service.AnalyticsService;
import com.example.nginep.auth.helpers.Claims;
import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.bookings.service.BookingService;
import com.example.nginep.property.dto.PropertyResponseDto;
import com.example.nginep.property.entity.Property;
import com.example.nginep.property.service.PropertyService;
import com.example.nginep.users.entity.Users;
import com.example.nginep.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final BookingService bookingService;
    private final PropertyService propertyService;
    private final UsersService usersService;

    @Override
    public BigDecimal calculateTotalEarnings() {
        return bookingService.calculateTotalEarnings();
    }

    @Override
    public Long calculateTotalBookings() {
        return bookingService.countTotalBookings();
    }

    @Override
    public Long calculateTotalProperties() {
        Users user = getCurrentUser();
        return propertyService.countPropertiesByTenant(user.getId());
    }

    @Override
    public BigDecimal calculatePeakSeasonRevenueDifference() {
        return bookingService.calculatePeakSeasonRevenueDifference();
    }

    @Override
    public OverviewReportDto getOverviewReport() {
        OverviewReportDto report = new OverviewReportDto();
        report.setTotalEarnings(calculateTotalEarnings());
        report.setTotalBookings(calculateTotalBookings());
        report.setTotalProperties(calculateTotalProperties());
        report.setPeakSeasonRevenueDifference(calculatePeakSeasonRevenueDifference());
        return report;
    }

    @Override
    public EarningsByTransactionDto getEarningsByTransaction(String interval, LocalDate startDate, LocalDate endDate) {
        List<Booking> bookings = bookingService.getConfirmedBookingsBetweenDatesForTenant(startDate, endDate);

        Map<LocalDate, BigDecimal> earningsByDate = bookings.stream()
                .collect(Collectors.groupingBy(
                        Booking::getCheckInDate,
                        Collectors.reducing(BigDecimal.ZERO, Booking::getFinalPrice, BigDecimal::add)
                ));

        List<EarningsByTransactionDto.EarningDataPoint> dataPoints = new ArrayList<>();
        BigDecimal totalEarnings = BigDecimal.ZERO;

        if ("daily".equals(interval)) {
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                BigDecimal amount = earningsByDate.getOrDefault(date, BigDecimal.ZERO);
                dataPoints.add(new EarningsByTransactionDto.EarningDataPoint(date.toString(), amount));
                totalEarnings = totalEarnings.add(amount);
            }
        } else if ("monthly".equals(interval)) {
            Map<String, BigDecimal> monthlyEarnings = earningsByDate.entrySet().stream()
                    .collect(Collectors.groupingBy(
                            entry -> entry.getKey().toString().substring(0, 7),
                            Collectors.reducing(BigDecimal.ZERO, Map.Entry::getValue, BigDecimal::add)
                    ));

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusMonths(1)) {
                String monthKey = date.toString().substring(0, 7);
                BigDecimal amount = monthlyEarnings.getOrDefault(monthKey, BigDecimal.ZERO);
                dataPoints.add(new EarningsByTransactionDto.EarningDataPoint(monthKey, amount));
                totalEarnings = totalEarnings.add(amount);
            }
        }

        EarningsByTransactionDto response = new EarningsByTransactionDto();
        response.setTotalEarnings(totalEarnings);
        response.setEarningsData(dataPoints);
        return response;
    }

    @Override
    public List<EarningsByPropertyDto> getEarningsByProperty() {
        List<PropertyResponseDto> properties = propertyService.getPropertyByTenantId();
        return properties.stream()
                .map(property -> {
                    BigDecimal earnings = bookingService.calculateTotalEarningsForProperty(property.getId());
                    return new EarningsByPropertyDto(property.getPropertyName(), earnings);
                })
                .sorted(Comparator.comparing(EarningsByPropertyDto::getEarnings).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyAvailabilityDto> getPropertyAvailability(LocalDate startDate, LocalDate endDate) {
        List<PropertyResponseDto> properties = propertyService.getPropertyByTenantId();

        return properties.stream().map(property -> {
            PropertyAvailabilityDto availabilityDto = new PropertyAvailabilityDto();
            availabilityDto.setPropertyId(property.getId());
            availabilityDto.setPropertyName(property.getPropertyName());

            List<PropertyAvailabilityDto.RoomAvailabilityDto> roomAvailabilities = property.getRooms().stream().map(room -> {
                PropertyAvailabilityDto.RoomAvailabilityDto roomAvailability = new PropertyAvailabilityDto.RoomAvailabilityDto();
                roomAvailability.setRoomId(room.getId());
                roomAvailability.setRoomName(room.getName());

                List<Booking> bookings = bookingService.getBookingsForRoomInDateRange(room.getId(), startDate, endDate);
                List<PropertyAvailabilityDto.UnavailableDateRangeDto> unavailableDates = bookings.stream()
                        .map(booking -> {
                            PropertyAvailabilityDto.UnavailableDateRangeDto dateRange = new PropertyAvailabilityDto.UnavailableDateRangeDto();
                            dateRange.setStartDate(booking.getCheckInDate().toString());
                            dateRange.setEndDate(booking.getCheckOutDate().toString());
                            return dateRange;
                        })
                        .collect(Collectors.toList());

                roomAvailability.setUnavailableDates(unavailableDates);
                return roomAvailability;
            }).collect(Collectors.toList());

            availabilityDto.setRooms(roomAvailabilities);
            return availabilityDto;
        }).collect(Collectors.toList());
    }

    private Users getCurrentUser() {
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        return usersService.getDetailUserByEmail(email);
    }
}