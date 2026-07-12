package com.assetflow.repository;

import com.assetflow.entity.BookingStatus;
import com.assetflow.entity.ResourceBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ResourceBookingRepository extends JpaRepository<ResourceBooking, Long> {

    @Query("SELECT b FROM ResourceBooking b WHERE b.asset.id = :assetId " +
           "AND b.status IN :activeStatuses " +
           "AND b.startTime < :endTime AND b.endTime > :startTime")
    List<ResourceBooking> findOverlappingBookings(
            @Param("assetId") Long assetId,
            @Param("activeStatuses") List<BookingStatus> activeStatuses,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    List<ResourceBooking> findByAssetIdOrderByStartTimeDesc(Long assetId);

    List<ResourceBooking> findByStatusIn(List<BookingStatus> statuses);
}
