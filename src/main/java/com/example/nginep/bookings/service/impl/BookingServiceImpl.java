package com.example.nginep.bookings.service.impl;

import com.example.nginep.auth.helpers.Claims;
import com.example.nginep.bookings.dto.*;
import com.example.nginep.bookings.entity.Booking;
import com.example.nginep.bookings.enums.BookingStatus;
import com.example.nginep.bookings.repository.BookingRepository;
import com.example.nginep.bookings.service.BookingService;
import com.example.nginep.bookings.tasks.CancelUnconfirmedBookingTask;
import com.example.nginep.bookings.tasks.CancelUnpaidBookingTask;
import com.example.nginep.exceptions.applicationException.ApplicationException;
import com.example.nginep.exceptions.notFoundException.NotFoundException;
import com.example.nginep.payments.entity.Payment;
import com.example.nginep.payments.enums.PaymentStatus;
import com.example.nginep.payments.enums.PaymentType;
import com.example.nginep.payments.service.PaymentService;
import com.example.nginep.peakSeasonRates.dto.PeakSeasonRatesResponseDto;
import com.example.nginep.peakSeasonRates.entity.PeakSeasonRates;
import com.example.nginep.peakSeasonRates.service.PeakSeasonRatesService;
import com.example.nginep.propertyImages.dto.PropertyImageResponseDto;
import com.example.nginep.propertyImages.service.PropertyImageService;
import com.example.nginep.rooms.entity.Room;
import com.example.nginep.rooms.service.RoomService;
import com.example.nginep.users.service.UsersService;
import com.example.nginep.property.entity.Property;
import com.example.nginep.users.entity.Users;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomService roomService;
    private final PaymentService paymentService;
    private final UsersService usersService;
    private final TaskScheduler taskScheduler;
    private final PropertyImageService propertyImageService;
    private final PeakSeasonRatesService peakSeasonRatesService;
    private final CancelUnpaidBookingTask cancelUnpaidBookingTask;
    private final CancelUnconfirmedBookingTask cancelUnconfirmedBookingTask;

    public BookingServiceImpl(@Lazy BookingRepository bookingRepository, @Lazy RoomService roomService, @Lazy PaymentService paymentService, UsersService usersService, TaskScheduler taskScheduler, @Lazy PropertyImageService propertyImageService, @Lazy PeakSeasonRatesService peakSeasonRatesService, @Lazy CancelUnpaidBookingTask cancelUnpaidBookingTask, @Lazy CancelUnconfirmedBookingTask cancelUnconfirmedBookingTask) {
        this.bookingRepository = bookingRepository;
        this.roomService = roomService;
        this.paymentService = paymentService;
        this.usersService = usersService;
        this.taskScheduler = taskScheduler;
        this.propertyImageService = propertyImageService;
        this.peakSeasonRatesService = peakSeasonRatesService;
        this.cancelUnpaidBookingTask = cancelUnpaidBookingTask;
        this.cancelUnconfirmedBookingTask = cancelUnconfirmedBookingTask;
    }

    @Override
    @Transactional
    public Booking createBooking(CreateBookingDto bookingDTO) {
        validateBookingDates(bookingDTO);
        validateRoomAvailability(bookingDTO);

        Users user = getCurrentUser();
        Room room = roomService.getRoomById(bookingDTO.getRoomId());

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(bookingDTO.getCheckInDate());
        booking.setCheckOutDate(bookingDTO.getCheckOutDate());
        booking.setNumGuests(bookingDTO.getNumGuests());
        booking.setUserMessage(bookingDTO.getUserMessage());
        booking.setStatus(BookingStatus.PENDING_PAYMENT);

        BigDecimal finalPrice = calculateFinalPrice(bookingDTO, room);
        booking.setFinalPrice(finalPrice);

        Booking savedBooking = bookingRepository.save(booking);

        scheduleUnpaidBookingCancellation(savedBooking.getId());

        return savedBooking;
    }

    @Override
    public Booking createNotAvailableBooking(CreateNotAvailableBookingDTO createNotAvailableBookingDTO) {
        return bookingRepository.save(createNotAvailableBookingDTO.toEntity());
    }

    private void validateBookingDates(CreateBookingDto bookingDTO) {
        LocalDate today = LocalDate.now();

        if (bookingDTO.getCheckInDate().isBefore(today) || bookingDTO.getCheckInDate().isEqual(today)) {
            throw new ApplicationException("Check-in date must be a future date");
        }

        if (bookingDTO.getCheckInDate().isEqual(bookingDTO.getCheckOutDate())) {
            throw new ApplicationException("Check-in and check-out dates cannot be the same");
        }

        if (bookingDTO.getCheckInDate().isAfter(bookingDTO.getCheckOutDate())) {
            throw new ApplicationException("Check-in date cannot be after check-out date");
        }

    }

    private BigDecimal calculateFinalPrice(CreateBookingDto bookingDTO, Room room) {
        BigDecimal adjustedBasePrice = calculateAdjustedBasePrice(room, bookingDTO.getCheckInDate());
        long numberOfNights = ChronoUnit.DAYS.between(bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate());
        return adjustedBasePrice.multiply(BigDecimal.valueOf(numberOfNights));
    }

    private BigDecimal calculateAdjustedBasePrice(Room room, LocalDate checkInDate) {
        BigDecimal basePrice = room.getBasePrice();
        Property property = room.getProperty();

        Optional<PeakSeasonRates> applicableRate = property.getPeakSeasonRates().stream()
                .filter(rate -> !checkInDate.isBefore(rate.getPeakSeasonDates().getFrom())
                        && !checkInDate.isAfter(rate.getPeakSeasonDates().getTo()))
                .findFirst();

        if (applicableRate.isPresent()) {
            PeakSeasonRates rate = applicableRate.get();
            if (rate.getRateType() == PeakSeasonRates.RateType.PERCENTAGE) {
                BigDecimal increase = basePrice.multiply(rate.getRateValue().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
                return basePrice.add(increase);
            } else if (rate.getRateType() == PeakSeasonRates.RateType.FIXED_AMOUNT) {
                return basePrice.add(rate.getRateValue());
            }
        }

        return basePrice;
    }

    private void validateRoomAvailability(CreateBookingDto bookingDTO) {
        boolean isOverlapping = bookingRepository.existsOverlappingBooking(
                bookingDTO.getRoomId(),
                bookingDTO.getCheckInDate(),
                bookingDTO.getCheckOutDate(),
                BookingStatus.CANCELLED
        );

        if (isOverlapping) {
            throw new ApplicationException("The room is not available for the selected dates.");
        }
    }

    @Override
    @Transactional
    public void updateBookingStatus(Long bookingId, BookingStatus status) {
        Booking booking = findBookingById(bookingId);
        booking.setStatus(status);
        bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getBookingByRoomId(Long roomId) {
        return bookingRepository.findAllByRoomId(roomId);
    }

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
    public void cancelBookingByTenant(Long bookingId) {
        Booking booking = findBookingById(bookingId);

        if (booking.getStatus() == BookingStatus.CONFIRMED || booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ApplicationException("Booking cannot be cancelled in its current state");
        }

        cancelBooking(booking);
    }

    @Override
    @Transactional
    public void cancelBookingByUser(Long bookingId) {
        Booking booking = findBookingById(bookingId);

        PaymentStatus paymentStatus = paymentService.getPaymentStatusForBooking(bookingId);
        if (paymentStatus != PaymentStatus.PENDING_PAYMENT && paymentStatus != PaymentStatus.REJECTED) {
            throw new ApplicationException("Booking cannot be cancelled as payment has already been initiated, confirmed, or canceled");
        }

        cancelBooking(booking);
    }

    private void cancelBooking(Booking booking) {
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    public Booking findBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));
    }

    @Override
    public Booking editNotAvailableBooking(CreateNotAvailableBookingDTO createNotAvailableBookingDTO) {
        Booking booking = bookingRepository.findById(createNotAvailableBookingDTO.getId()).orElseThrow(()->new NotFoundException("Booking with id: " + createNotAvailableBookingDTO.getId() + " not found"));
        booking.setCheckInDate(createNotAvailableBookingDTO.getFrom());
        booking.setCheckOutDate(createNotAvailableBookingDTO.getTo());
        return bookingRepository.save(booking);
    }

    @Override
    public String deleteNotAvailableBooking(Long bookingId) {
        bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking with id: " + bookingId + " not found"));
        bookingRepository.deleteById(bookingId);
        return "Delete Booking with id " + bookingId + " success";
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

    @Override
    public List<UserBookingsDto> getUserBookings() {
        Users user = getCurrentUser();
        List<Booking> bookings = bookingRepository.findByUser(user);

        return bookings.stream()
                .map(this::mapToUserBookingResponseDto)
                .collect(Collectors.toList());
    }

    private UserBookingsDto mapToUserBookingResponseDto(Booking booking) {
        UserBookingsDto dto = new UserBookingsDto();
        dto.setBookingId(booking.getId());
        dto.setRoomId(booking.getRoom().getId());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setNumGuests(booking.getNumGuests());
        dto.setStatus(booking.getStatus());
        dto.setHostName(booking.getRoom().getProperty().getUser().getFullName());
        dto.setRoomName(booking.getRoom().getName());
        dto.setPropertyName(booking.getRoom().getProperty().getPropertyName());
        dto.setPropertyAddress(booking.getRoom().getProperty().getPropertyAddress());
        dto.setPropertyCity(booking.getRoom().getProperty().getPropertyCity());
        dto.setPropertyProvince(booking.getRoom().getProperty().getPropertyProvince());
        dto.setPropertyCoverImage(getCoverImage(booking.getRoom().getProperty().getId()));
        return dto;
    }

    @Override
    public List<TenantBookingsDto> getTenantBookings() {
        Users user = getCurrentUser();
        List<Booking> bookings = bookingRepository.findByTenant(user.getId());

        return bookings.stream()
                .map(this::mapToTenantBookingResponseDto)
                .collect(Collectors.toList());
    }

    private TenantBookingsDto mapToTenantBookingResponseDto(Booking booking) {
        TenantBookingsDto dto = new TenantBookingsDto();
        dto.setBookingId(booking.getId());
        dto.setRoomId(booking.getRoom().getId());
        dto.setPaymentId(booking.getPayment().getId());
        dto.setPropertyName(booking.getRoom().getProperty().getPropertyName());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setGuestName(booking.getUser().getFullName());
        dto.setNumGuests(booking.getNumGuests());
        dto.setRoomName(booking.getRoom().getName());
        dto.setFinalPrice(booking.getFinalPrice());
        dto.setStatus(booking.getStatus());
        dto.setPaymentType(booking.getPayment().getPaymentType());
        dto.setPaymentStatus(booking.getPayment().getStatus());
        dto.setProofOfPayment(booking.getPayment().getProofOfPayment());
        dto.setPropertyCoverImage(getCoverImage(booking.getRoom().getProperty().getId()));
        return dto;
    }

    @Override
    public BookingPaymentDetailsDto getBookingPaymentDetails(Long bookingId) {
        Booking booking = findBookingById(bookingId);
        Payment payment = booking.getPayment();
        Room room = booking.getRoom();
        Property property = room.getProperty();
        Users tenant = property.getUser();

        BookingPaymentDetailsDto dto = new BookingPaymentDetailsDto();
        dto.setBookingId(booking.getId());
        dto.setRoomId(room.getId());
        dto.setPaymentId(payment.getId());
        dto.setFinalPrice(booking.getFinalPrice());
        dto.setPaymentStatus(payment.getStatus());
        dto.setExpiryTime(payment.getExpiryTime());
        dto.setPropertyName(property.getPropertyName());
        dto.setRoomName(room.getName());
        dto.setPropertyAddress(property.getPropertyAddress());
        dto.setPropertyCity(property.getPropertyCity());
        dto.setPropertyProvince(property.getPropertyProvince());
        dto.setCoverImage(getCoverImage(booking.getRoom().getProperty().getId()));
        dto.setPaymentType(payment.getPaymentType());


        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setNumGuests(booking.getNumGuests());
        dto.setBasePrice(calculateAdjustedBasePrice(room, booking.getCheckInDate()));

        if (payment.getPaymentType() == PaymentType.MANUAL_PAYMENT) {
            dto.setBankName(tenant.getBankName());
            dto.setBankAccountNumber(tenant.getBankAccountNumber());
            dto.setBankHolderName(tenant.getBankHolderName());
        } else if (payment.getPaymentType() == PaymentType.AUTOMATIC_PAYMENT) {
            dto.setSpecificPaymentType(payment.getSpecificPaymentType());
            dto.setVaNumber(payment.getVaNumber());
            dto.setBillKey(payment.getBillKey());
            dto.setBillerCode(payment.getBillerCode());
            dto.setQrisUrl(payment.getQrisUrl());
        }

        return dto;
    }

    @Override
    public Long checkExistingPendingBooking(Long roomId) {
        Users user = getCurrentUser();
        Room room = roomService.getRoomById(roomId);

        return bookingRepository.findByUserAndRoomAndStatus(user, room, BookingStatus.PENDING_PAYMENT)
                .map(Booking::getId)
                .orElse(null);
    }

    private void scheduleUnpaidBookingCancellation(Long bookingId) {
        cancelUnpaidBookingTask.setBookingId(bookingId);
        taskScheduler.schedule(cancelUnpaidBookingTask, Instant.now().plus(1, ChronoUnit.HOURS));
    }

    @Override
    @Transactional
    public void cancelBookingIfPending(Long bookingId) {
        Booking booking = findBookingById(bookingId);
        if (booking.getStatus() == BookingStatus.PENDING_PAYMENT && booking.getPayment().getAttempts() == 0) {
            booking.setStatus(BookingStatus.CANCELLED);
            Payment payment = booking.getPayment();
            if (payment != null) {
                payment.setStatus(PaymentStatus.CANCELLED);
            }
            bookingRepository.save(booking);
        }
    }

    @Override
    public List<UnreviewedBookingDto> getUnreviewedBookingsForUser() {
        Users user = getCurrentUser();
        List<Booking> bookings = bookingRepository.findUnreviewedBookingsForUser(user.getId());
        return bookings.stream().map(this::mapToUnreviewedBookingDto).collect(Collectors.toList());
    }

    private UnreviewedBookingDto mapToUnreviewedBookingDto(Booking booking) {
        UnreviewedBookingDto dto = new UnreviewedBookingDto();
        dto.setId(booking.getId());
        dto.setPropertyName(booking.getRoom().getProperty().getPropertyName());
        dto.setRoomName(booking.getRoom().getName());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setPropertyCoverImage(getCoverImage(booking.getRoom().getProperty().getId()));
        return dto;
    }

    private String getCoverImage(Long propertyId) {
        List<PropertyImageResponseDto> propertyImages = propertyImageService.getPropertyImageByPropertyId(propertyId);
        return propertyImages.stream()
                .filter(PropertyImageResponseDto::getIsThumbnail)
                .findFirst()
                .map(PropertyImageResponseDto::getPath)
                .orElse(null);
    }

    @Override
    public BigDecimal calculateTotalEarnings() {
        Users user = getCurrentUser();
        return bookingRepository.calculateTotalEarningsByTenantId(user.getId());
    }

    @Override
    public Long countTotalBookings() {
        Users user = getCurrentUser();
        return bookingRepository.countBookingsByTenantId(user.getId());
    }

    @Override
    public BigDecimal calculatePeakSeasonRevenueDifference() {
        Users user = getCurrentUser();
        List<Booking> bookings = bookingRepository.findConfirmedBookingsByTenant(user.getId());
        BigDecimal revenueDifference = BigDecimal.ZERO;

        for (Booking booking : bookings) {
            List<PeakSeasonRatesResponseDto> peakSeasonRates = peakSeasonRatesService.getPeakSeasonRatesByPropertyId(booking.getRoom().getProperty().getId());

            LocalDate currentDate = booking.getCheckInDate();
            while (!currentDate.isAfter(booking.getCheckOutDate())) {
                for (PeakSeasonRatesResponseDto peakSeason : peakSeasonRates) {
                    if (currentDate.isEqual(peakSeason.getPeakSeasonDates().getFrom()) ||
                            (currentDate.isAfter(peakSeason.getPeakSeasonDates().getFrom()) &&
                                    currentDate.isBefore(peakSeason.getPeakSeasonDates().getTo())) ||
                            currentDate.isEqual(peakSeason.getPeakSeasonDates().getTo())) {

                        BigDecimal basePrice = booking.getRoom().getBasePrice();
                        BigDecimal peakSeasonPrice = calculatePeakSeasonPrice(basePrice, peakSeason);
                        revenueDifference = revenueDifference.add(peakSeasonPrice.subtract(basePrice));
                        break;
                    }
                }
                currentDate = currentDate.plusDays(1);
            }
        }

        return revenueDifference;
    }

    private BigDecimal calculatePeakSeasonPrice(BigDecimal basePrice, PeakSeasonRatesResponseDto peakSeason) {
        if (peakSeason.getRateType() == PeakSeasonRates.RateType.PERCENTAGE) {
            BigDecimal percentage = BigDecimal.ONE.add(peakSeason.getRateValue().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
            return basePrice.multiply(percentage);
        } else {
            return basePrice.add(peakSeason.getRateValue());
        }
    }

    @Override
    public List<Booking> getConfirmedBookingsBetweenDatesForTenant(LocalDate startDate, LocalDate endDate) {
        Users user = getCurrentUser();
        return bookingRepository.findConfirmedBookingsBetweenDatesForTenant(user.getId(), BookingStatus.CONFIRMED, startDate, endDate);
    }

    @Override
    public BigDecimal calculateTotalEarningsForProperty(Long propertyId) {
        return bookingRepository.findByRoomPropertyIdAndStatus(propertyId, BookingStatus.CONFIRMED).stream()
                .map(Booking::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<Booking> getBookingsForRoomInDateRange(Long roomId, LocalDate startDate, LocalDate endDate) {
        return bookingRepository.findConfirmedAndNotAvailableByRoomIdAndDateRange(roomId, startDate, endDate);
    }

    @Override
    public void scheduleUnconfirmedBookingCancellation(Long bookingId) {
        cancelUnconfirmedBookingTask.setBookingId(bookingId);
        taskScheduler.schedule(cancelUnconfirmedBookingTask,
                Instant.now().plus(48, ChronoUnit.HOURS));
    }


    @Override
    @Transactional
    public void cancelBookingIfNotConfirmed(Long bookingId) {
        Booking booking = findBookingById(bookingId);
        if (booking.getStatus() == BookingStatus.AWAITING_CONFIRMATION) {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            //How do we refund the paid booking? dang
        }
    }

    private Users getCurrentUser() {
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        return usersService.getDetailUserByEmail(email);
    }

}