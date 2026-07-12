package com.assetflow.controller;

import com.assetflow.dto.request.BookingRequest;
import com.assetflow.dto.response.BookingResponse;
import com.assetflow.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public BookingResponse bookResource(
            @Valid @RequestBody BookingRequest request,
            Principal principal
    ) {
        return bookingService.bookResource(request, principal.getName());
    }

    @GetMapping("/asset/{assetId}")
    @PreAuthorize("isAuthenticated()")
    public List<BookingResponse> getBookingsForAsset(
            @PathVariable Long assetId
    ) {
        return bookingService.getBookingsForAsset(assetId);
    }

    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    public List<BookingResponse> getActiveBookings() {
        return bookingService.getAllActiveBookings();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<BookingResponse> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public BookingResponse cancelBooking(
            @PathVariable Long id,
            Principal principal
    ) {
        return bookingService.cancelBooking(id, principal.getName());
    }
}
