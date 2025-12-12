package com.app.UberBookingService.dto;

import com.app.UberEntityService.models.ExactLocation;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBookingDto {
    private Long passengerId;
    private Long distance;
    private ExactLocation startLocation;
    private ExactLocation endLocation;

    @PrePersist
    public void calculateDistance() {
        //using haversine formula to calculate the distance between two points on the earth
        if (startLocation != null && endLocation != null) {
            double lat1 = Math.toRadians(startLocation.getLatitude());
            double lon1 = Math.toRadians(startLocation.getLongitude());
            double lat2 = Math.toRadians(endLocation.getLatitude());
            double lon2 = Math.toRadians(endLocation.getLongitude());

            double dLat = lat2 - lat1;
            double dLon = lon2 - lon1;

            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                    + Math.cos(lat1) * Math.cos(lat2)
                    * Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            double R = 6371.0; // earth's radius in km
            double distanceKm = R * c;

            this.distance = Math.round(distanceKm); // store as whole km
        }
    }
}
