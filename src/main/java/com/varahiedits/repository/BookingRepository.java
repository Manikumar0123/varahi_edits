package com.varahiedits.repository;

import com.varahiedits.model.Booking;
import com.varahiedits.model.Booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByStatusOrderByCreatedAtDesc(BookingStatus status);

    List<Booking> findAllByOrderByCreatedAtDesc();

    List<Booking> findByServiceOrderByCreatedAtDesc(String service);

    long countByStatus(BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.createdAt BETWEEN :start AND :end ORDER BY b.createdAt DESC")
    List<Booking> findByDateRange(LocalDateTime start, LocalDateTime end);

    @Query("SELECT b.service, COUNT(b) FROM Booking b GROUP BY b.service")
    List<Object[]> countByService();

    boolean existsByEmailAndService(String email, String service);
}
