package com.varahiedits.service;

import com.varahiedits.dto.BookingRequest;
import com.varahiedits.model.Booking;
import com.varahiedits.model.Booking.BookingStatus;
import com.varahiedits.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    
    /**
     * Create a new booking from contact form submission
     */
    @Transactional
    public Booking createBooking(BookingRequest request) {
        Booking booking = Booking.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .service(request.getService())
                .message(request.getMessage())
                .status(BookingStatus.PENDING)
                .build();

        Booking saved = bookingRepository.save(booking);
        log.info("New booking created: ID={}, Service={}, Customer={}", saved.getId(), saved.getService(), saved.getName());

        // Send notifications asynchronously (won't block response)
        emailService.sendBookingConfirmationToCustomer(saved);
        emailService.sendBookingAlertToOwner(saved);
        notificationService.sendWhatsAppAlertToOwner(saved);
        notificationService.sendWhatsAppConfirmationToCustomer(saved);

        return saved;
    }

    /**
     * Get all bookings (Admin)
     */
    public List<Booking> getAllBookings() {
        return bookingRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Get booking by ID
     */
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    /**
     * Get bookings by status (Admin filter)
     */
    public List<Booking> getBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    /**
     * Update booking status (Admin)
     */
    @Transactional
    public Booking updateStatus(Long id, BookingStatus newStatus, String adminNotes) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + id));

        BookingStatus oldStatus = booking.getStatus();
        booking.setStatus(newStatus);
        if (adminNotes != null && !adminNotes.isBlank()) {
            booking.setAdminNotes(adminNotes);
        }
        Booking updated = bookingRepository.save(booking);
        log.info("Booking #{} status updated: {} → {}", id, oldStatus, newStatus);

        // Notify customer of status change
        emailService.sendStatusUpdateToCustomer(updated);

        return updated;
    }

    /**
     * Delete a booking (Admin)
     */
    @Transactional
    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new RuntimeException("Booking not found with ID: " + id);
        }
        bookingRepository.deleteById(id);
        log.info("Booking #{} deleted", id);
    }

    /**
     * Dashboard statistics (Admin)
     */
//    public Map<String, Object> getDashboardStats() {
//        Map<String, Object> stats = new HashMap<>();
//        stats.put("total", bookingRepository.count());
//        stats.put("pending", bookingRepository.countByStatus(BookingStatus.PENDING));
//        stats.put("confirmed", bookingRepository.countByStatus(BookingStatus.CONFIRMED));
//        stats.put("inProgress", bookingRepository.countByStatus(BookingStatus.IN_PROGRESS));
//        stats.put("completed", bookingRepository.countByStatus(BookingStatus.COMPLETED));
//        stats.put("cancelled", bookingRepository.countByStatus(BookingStatus.CANCELLED));
//
//        // Bookings by service
//        List<Object[]> serviceStats = bookingRepository.countByService();
//        Map<String, Long> byService = new HashMap<>();
//        for (Object[] row : serviceStats) {
//            byService.put((String) row[0], (Long) row[1]);
//        }
//        stats.put("byService", byService);
//
//        // Recent bookings (last 5)
//        stats.put("recentBookings", bookingRepository.findAllByOrderByCreatedAtDesc()
//                .stream().limit(5).toList());
//
//        return stats;
//    }

    /**
     * Get bookings in date range (Admin)
     */
    public List<Booking> getBookingsByDateRange(LocalDateTime start, LocalDateTime end) {
        return bookingRepository.findByDateRange(start, end);
    }
    public Map<String, Object> getDashboardStats() {

        List<Booking> bookings = bookingRepository.findAll();

        long total = bookings.size();

        long pending = bookings.stream()
                .filter(b -> b.getStatus() != null && b.getStatus() == BookingStatus.PENDING)
                .count();

        long confirmed = bookings.stream()
                .filter(b -> b.getStatus() != null && b.getStatus() == BookingStatus.CONFIRMED)
                .count();

        long completed = bookings.stream()
                .filter(b -> b.getStatus() != null && b.getStatus() == BookingStatus.COMPLETED)
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("pending", pending);
        stats.put("confirmed", confirmed);
        stats.put("completed", completed);

        return stats;
    }
}
