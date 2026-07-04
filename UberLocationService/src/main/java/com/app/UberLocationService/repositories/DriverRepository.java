package com.app.UberLocationService.repositories;

import com.app.UberEntityService.models.Driver;
import com.app.UberEntityService.models.DriverApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByIdAndDriverApprovalStatus(
            Long id,
            DriverApprovalStatus driverApprovalStatus
    );
}
