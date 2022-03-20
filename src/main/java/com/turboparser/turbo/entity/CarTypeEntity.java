package com.turboparser.turbo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="CarTypeEntity", schema = "TURBODB")
public class CarTypeEntity {

    @javax.persistence.Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    public String make;
    public String model;
    public String price_from;
    public String price_to;
    public String year_from;
    public String year_to;
    public String engine_volume_from;
    public String engine_volume_to;
    public String mileage_from;
    public String mileage_to;
    public String fuel_type;
    public String transmission;
}