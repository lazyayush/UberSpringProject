package com.app.UberEntityService.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Color extends BaseModel{
    @Column(unique = true, nullable = false)
    private String name;
}
