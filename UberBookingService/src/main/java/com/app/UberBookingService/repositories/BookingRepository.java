package com.app.UberBookingService.repositories;

import com.app.UberEntityService.models.Booking;
import com.app.UberEntityService.models.BookingStatus;
import com.app.UberEntityService.models.Driver;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Booking b SET b.bookingStatus = :status, b.driver = :driver " +
            "WHERE b.id = :id AND b.bookingStatus = :expectedStatus")
    int updateBookingStatusAndDriverById(@Param("id") Long id,
                                          @Param("status")BookingStatus status,
                                          @Param("driver")Driver driver,
                                          @Param("expectedStatus") BookingStatus expectedStatus);
}
