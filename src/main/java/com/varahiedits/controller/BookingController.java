package com.varahiedits.controller;

import com.varahiedits.dto.ApiResponse;
import com.varahiedits.dto.BookingRequest;
import com.varahiedits.model.Booking;
import com.varahiedits.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

	@Autowired
    private final BookingService bookingService;

    /**
     * POST /api/bookings/submit
     * Public endpoint – submit contact form / booking
     */
    @PostMapping("/submit")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Map<String, Object>> submitBooking(@Valid @RequestBody BookingRequest request) {
        Booking booking = bookingService.createBooking(request);
        return ApiResponse.success(
            "Your booking has been received! We will contact you within 24 hours.",
            Map.of(
                "bookingId", booking.getId(),
                "status", booking.getStatus(),
                "service", booking.getService()
            )
        );
    }
}
