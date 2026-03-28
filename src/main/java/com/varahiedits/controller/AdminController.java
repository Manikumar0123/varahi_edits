package com.varahiedits.controller;

import com.varahiedits.dto.ApiResponse;
import com.varahiedits.model.Booking;
import com.varahiedits.model.Booking.BookingStatus;
import com.varahiedits.service.BookingService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

	@Autowired
    private final BookingService bookingService;

    /**
     * GET /api/admin/dashboard
     * Returns summary stats for admin dashboard
     */
	
    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> getDashboard() {
        return ApiResponse.success("Dashboard stats", bookingService.getDashboardStats());
    }

    /**
     * GET /api/admin/bookings
     * Get all bookings (optional filter by status)
     */
    @GetMapping("/bookings")
    public ApiResponse<List<Booking>> getAllBookings(
            @RequestParam(required = false) BookingStatus status) {
        List<Booking> bookings = (status != null)
                ? bookingService.getBookingsByStatus(status)
                : bookingService.getAllBookings();
        return ApiResponse.success("Bookings fetched", bookings);
    }

    /**
     * GET /api/admin/bookings/{id}
     * Get single booking by ID
     */
    @GetMapping("/bookings/{id}")
    public ApiResponse<Booking> getBooking(@PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + id));
        return ApiResponse.success("Booking found", booking);
    }

    /**
     * PATCH /api/admin/bookings/{id}/status
     * Update booking status
     * Body: { "status": "CONFIRMED", "adminNotes": "Optional note" }
     */
    @PatchMapping("/bookings/{id}/status")
    public ApiResponse<Booking> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        BookingStatus newStatus = BookingStatus.valueOf(body.get("status").toUpperCase());
        String notes = body.get("adminNotes");
        Booking updated = bookingService.updateStatus(id, newStatus, notes);
        return ApiResponse.success("Status updated to " + newStatus, updated);
    }

    /**
     * DELETE /api/admin/bookings/{id}
     * Delete a booking
     */
    @DeleteMapping("/bookings/{id}")
    public ApiResponse<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ApiResponse.success("Booking #" + id + " deleted", null);
    }

    /**
     * GET /api/admin/bookings/range?start=...&end=...
     * Get bookings in a date range
     */
    @GetMapping("/bookings/range")
    public ApiResponse<List<Booking>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ApiResponse.success("Bookings in range", bookingService.getBookingsByDateRange(start, end));
    }
}
