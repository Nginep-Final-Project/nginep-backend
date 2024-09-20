package com.example.nginep.bookings.service.impl;

import com.example.nginep.bookings.dto.CreateBookingDTO;
import com.example.nginep.bookings.dto.CreateNotAvailableBookingDTO;
import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.bookings.enums.BookingStatus;
import com.example.nginep.bookings.repository.BookingRepository;
import com.example.nginep.bookings.service.BookingService;
import com.example.nginep.exceptions.applicationException.ApplicationException;
import com.example.nginep.exceptions.notFoundException.NotFoundException;
import com.example.nginep.payments.entity.Payment;
import com.example.nginep.payments.enums.PaymentStatus;
import com.example.nginep.payments.service.PaymentService;
import com.example.nginep.rooms.entity.Room;
import com.example.nginep.rooms.service.RoomService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomService roomService;
    private final PaymentService paymentService;

    @Override
    @Transactional
    public Booking createBooking(CreateBookingDTO bookingDTO) {
        validateBookingDates(bookingDTO);
        Room room = roomService.getRoomById(bookingDTO.getRoomId());

        //TODO: Make the service later for user checking, or should be by authorization
//        if (!userService.existsById(bookingDTO.getUserId())) {
//            throw new NotFoundException("User with id " + bookingDTO.getUserId() + " not found");
//        }

        Booking booking = new Booking();
        booking.setUserId(bookingDTO.getUserId());
        booking.setRoomId(bookingDTO.getRoomId());
        booking.setCheckInDate(bookingDTO.getCheckInDate());
        booking.setCheckOutDate(bookingDTO.getCheckOutDate());
        booking.setNumGuests(bookingDTO.getNumGuests());
        booking.setUserMessage(bookingDTO.getUserMessage());
        booking.setStatus(BookingStatus.PENDING_PAYMENT);

        BigDecimal finalPrice = calculateFinalPrice(bookingDTO, room);
        booking.setFinalPrice(finalPrice);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking createNotAvailableBooking(CreateNotAvailableBookingDTO createNotAvailableBookingDTO) {
        return bookingRepository.save(createNotAvailableBookingDTO.toEntity());
    }

    private void validateBookingDates(CreateBookingDTO bookingDTO) {
        if (bookingDTO.getCheckInDate().isAfter(bookingDTO.getCheckOutDate())) {
            throw new ApplicationException("Check-in date cannot be after check-out date");
        }
    }

    private BigDecimal calculateFinalPrice(CreateBookingDTO bookingDTO, Room room) {
        BigDecimal basePrice = room.getBasePrice();
        long numberOfNights = ChronoUnit.DAYS.between(bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate());
        return basePrice.multiply(BigDecimal.valueOf(numberOfNights));
    }

    @Override
    @Transactional
    public void updateBookingStatus(Long bookingId, BookingStatus status) {
        Booking booking = findBookingById(bookingId);
        booking.setStatus(status);
        bookingRepository.save(booking);
    }

//    @Override
//    public List<Booking> getTenantBookings(Long tenantId, BookingStatus status) {
//        return bookingRepository.findByTenantIdAndStatus(tenantId, status);
//    }

    @Override
    @Transactional
    public Booking confirmBooking(Long bookingId) {
        Booking booking = findBookingById(bookingId);

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            booking.setStatus(BookingStatus.CONFIRMED);
            return bookingRepository.save(booking);
        } else {
            throw new ApplicationException("Booking is already confirmed");
        }
    }

    @Override
    @Transactional
    public Booking cancelBookingByTenant(Long bookingId) {
        Booking booking = findBookingById(bookingId);

        if (booking.getStatus() == BookingStatus.CONFIRMED || booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ApplicationException("Booking cannot be cancelled in its current state");
        }

        return cancelBooking(booking);
    }

    @Override
    @Transactional
    public Booking cancelBookingByUser(Long bookingId) {
        Booking booking = findBookingById(bookingId);

        PaymentStatus paymentStatus = paymentService.getPaymentStatusForBooking(bookingId);
        if (paymentStatus != PaymentStatus.PENDING_PAYMENT && paymentStatus != PaymentStatus.REJECTED) {
            throw new ApplicationException("Booking cannot be cancelled as payment has already been initiated, confirmed, or canceled");
        }

        return cancelBooking(booking);
    }

    private Booking cancelBooking(Booking booking) {
        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    private Booking findBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));
    }

    @Override
    @Transactional
    public Booking updateBookingStatusMidtrans(String orderId, String transactionStatus, String fraudStatus) {
        Booking booking = findBookingById(Long.valueOf(orderId));

        switch (transactionStatus) {
            case "capture":
                if ("challenge".equals(fraudStatus)) {
                    booking.setStatus(BookingStatus.AWAITING_CONFIRMATION);
                } else if ("accept".equals(fraudStatus)) {
                    booking.setStatus(BookingStatus.AWAITING_CONFIRMATION);
                }
                break;
            case "settlement":
                booking.setStatus(BookingStatus.AWAITING_CONFIRMATION);
                break;
            case "deny":
            case "cancel":
            case "expire":
                booking.setStatus(BookingStatus.CANCELLED);
                break;
            case "pending":
                booking.setStatus(BookingStatus.PENDING_PAYMENT);
                break;
            default:
                throw new ApplicationException("Unhandled transaction status: " + transactionStatus);
        }
        return bookingRepository.save(booking);
    }
}