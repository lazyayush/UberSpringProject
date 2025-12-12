package com.app.UberEntityService.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Car extends BaseModel{

    @Column(unique = true, nullable = false)
    private String plateNumber;

    @Enumerated(value = EnumType.STRING)
    private CarType carType;

    @ManyToOne
    private Color color;

    private String brand;
    private String model;

    @OneToOne
    private Driver driver;
}
