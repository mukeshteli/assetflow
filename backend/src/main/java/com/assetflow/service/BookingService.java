package com.assetflow.service;

import com.assetflow.dto.request.BookingRequest;
import com.assetflow.dto.response.BookingResponse;
import com.assetflow.entity.*;
import com.assetflow.exception.ApiException;
import com.assetflow.exception.ResourceNotFoundException;
import com.assetflow.repository.AssetRepository;
import com.assetflow.repository.EmployeeRepository;
import com.assetflow.repository.ResourceBookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private final ResourceBookingRepository bookingRepository;
    private final AssetRepository assetRepository;
    private final EmployeeRepository employeeRepository;
    private final NotificationService notificationService;
    private final ActivityLogService activityLogService;

    public BookingResponse bookResource(BookingRequest request, String actorEmail) {
        Employee actor = employeeRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", actorEmail));

        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset", request.getAssetId()));

        if (!Boolean.TRUE.equals(asset.getIsBookable())) {
            throw new ApiException("Asset " + asset.getAssetTag() + " is not configured as a bookable resource.", HttpStatus.BAD_REQUEST);
        }

        if (request.getStartTime().isAfter(request.getEndTime()) || request.getStartTime().isEqual(request.getEndTime())) {
            throw new ApiException("Start time must be before end time.", HttpStatus.BAD_REQUEST);
        }

        // Overlap validation
        List<ResourceBooking> overlaps = bookingRepository.findOverlappingBookings(
                asset.getId(),
                List.of(BookingStatus.UPCOMING, BookingStatus.ONGOING),
                request.getStartTime(),
                request.getEndTime()
        );

        if (!overlaps.isEmpty()) {
            throw new ApiException("Conflict: The requested time slot is unavailable.", HttpStatus.CONFLICT);
        }

        ResourceBooking booking = ResourceBooking.builder()
                .asset(asset)
                .bookedBy(actor)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .purpose(request.getPurpose())
                .status(BookingStatus.UPCOMING)
                .build();

        booking = bookingRepository.save(booking);

        activityLogService.log(actor, "BOOK_RESOURCE", "Booked resource " + asset.getAssetTag() + " from " + request.getStartTime() + " to " + request.getEndTime());
        notificationService.sendNotification(actor, "Booking confirmed for " + asset.getAssetName() + " on " + request.getStartTime().toLocalDate(), "BOOKING_CONFIRMED");

        return mapToResponse(booking);
    }

    public BookingResponse cancelBooking(Long bookingId, String actorEmail) {
        Employee actor = employeeRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", actorEmail));

        ResourceBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("ResourceBooking", bookingId));

        if (booking.getStatus() != BookingStatus.UPCOMING) {
            throw new ApiException("Only upcoming bookings can be cancelled.", HttpStatus.BAD_REQUEST);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        activityLogService.log(actor, "CANCEL_BOOKING", "Cancelled booking of " + booking.getAsset().getAssetTag());
        notificationService.sendNotification(booking.getBookedBy(), "Your booking for " + booking.getAsset().getAssetName() + " has been cancelled.", "BOOKING_CANCELLED");

        return mapToResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsForAsset(Long assetId) {
        return bookingRepository.findByAssetIdOrderByStartTimeDesc(assetId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getAllActiveBookings() {
        return bookingRepository.findByStatusIn(List.of(BookingStatus.UPCOMING, BookingStatus.ONGOING)).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    private BookingResponse mapToResponse(ResourceBooking b) {
        return BookingResponse.builder()
                .id(b.getId())
                .assetId(b.getAsset().getId())
                .assetTag(b.getAsset().getAssetTag())
                .assetName(b.getAsset().getAssetName())
                .bookedById(b.getBookedBy().getId())
                .bookedByName(b.getBookedBy().getFullName())
                .startTime(b.getStartTime())
                .endTime(b.getEndTime())
                .status(b.getStatus())
                .purpose(b.getPurpose())
                .createdAt(b.getCreatedAt())
                .build();
    }
}
